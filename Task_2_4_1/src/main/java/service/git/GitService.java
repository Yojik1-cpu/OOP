package service.git;

import model.GlobalSettings;
import service.utils.ProcessUtils;
import service.utils.SimpleLogger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GitService {
    public void verifyGitConfig() {
        try {
            ProcessUtils.CommandResult userName = ProcessUtils.
                    runCommand(List.of("git", "config", "--get", "user.name"), null, 30);
            ProcessUtils.CommandResult helper = ProcessUtils.
                    runCommand(List.of("git", "config", "--get", "credential.helper"), null, 30);
            ProcessUtils.CommandResult askPass = ProcessUtils.
                    runCommand(List.of("git", "config", "--get", "core.askPass"), null, 30);
            if (userName.exitCode != 0 || userName.output.trim().isEmpty()) {
                throw new IllegalStateException("Git is not configured: `git config --get user.name` is empty.");
            }
            boolean nonInteractive = (helper.exitCode == 0 && !helper.output.trim().isEmpty())
                    || (askPass.exitCode == 0 && !askPass.output.trim().isEmpty());
            if (!nonInteractive) {
                throw new IllegalStateException("Git auth may become interactive. " +
                        "Configure credential.helper or core.askPass.");
            }
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Failed to verify git config: " + e.getMessage(), e);
        }
    }

    public boolean prepareRepository(String repoUrl, Path repoDir, GlobalSettings settings)
            throws IOException, InterruptedException {
        long timeout = Optional.ofNullable(settings.getGitTimeoutSeconds()).orElse(180L);

        if (Files.exists(repoDir.resolve(".git"))) {
            SimpleLogger.info("Repository exists, fetching updates for " + repoUrl);
            ProcessUtils.CommandResult fetchResult = ProcessUtils
                    .runCommand(List.of("git", "fetch"), repoDir, timeout);
            if (fetchResult.exitCode != 0) {
                SimpleLogger.error("Failed to fetch repository " + repoUrl + ": " + fetchResult.output);
                return false;
            }
            return checkoutMainOrMaster(repoDir, timeout, true);
        } else {
            SimpleLogger.info("Cloning repository " + repoUrl);
            ProcessUtils.CommandResult clone = ProcessUtils
                    .runCommand(List.of("git", "clone", repoUrl, repoDir.toString()), null, timeout);
            if (clone.exitCode != 0) {
                SimpleLogger.error("Failed to clone repository " + repoUrl + ": " + clone.output);
                return false;
            }
            return checkoutMainOrMaster(repoDir, timeout, false);
        }
    }

    public List<LocalDate> getCommitDates(Path repoDir) {
        List<LocalDate> dates = new ArrayList<>();
        try {
            ProcessUtils.CommandResult result = ProcessUtils
                    .runCommand(List.of("git", "log", "--all", "--pretty=format:%as"), repoDir, 30);
            if (result.exitCode == 0 && !result.output.trim().isEmpty()) {
                String[] lines = result.output.split("\n");
                for (String line : lines) {
                    try {
                        dates.add(LocalDate.parse(line.trim()));
                    } catch (Exception e) {}
                }
            }
        } catch (IOException | InterruptedException e) {
            SimpleLogger.warn("Failed to get commit dates for " + repoDir + ": " + e.getMessage());
        }
        return dates;
    }

    public LocalDate getFirstCommitDate(Path repoDir, String relativePath) {
        try {
            ProcessUtils.CommandResult result = ProcessUtils
                    .runCommand(List.of("git", "log", "--all", "--reverse", "--format=%as", "--", relativePath),
                            repoDir, 30);
            if (result.exitCode == 0 && !result.output.trim().isEmpty()) {
                String firstLine = result.output.split("\n")[0].trim();
                return LocalDate.parse(firstLine);
            }
        } catch (Exception e) {
            SimpleLogger.warn("Failed to get first commit date for " + relativePath + ": " + e.getMessage());
        }
        return null;
    }

    public LocalDate getLastCommitDate(Path repoDir, String relativePath) {
        try {
            ProcessUtils.CommandResult result = ProcessUtils
                    .runCommand(List.of("git", "log", "--all", "-1", "--format=%as", "--", relativePath),
                            repoDir, 30);
            if (result.exitCode == 0 && !result.output.trim().isEmpty()) {
                String firstLine = result.output.split("\n")[0].trim();
                return LocalDate.parse(firstLine);
            }
        } catch (Exception e) {
            SimpleLogger.warn("Failed to get last commit date for " + relativePath + ": " + e.getMessage());
        }
        return null;
    }

    private boolean checkoutMainOrMaster(Path repoDir, long timeoutSeconds, boolean isUpdate)
            throws IOException, InterruptedException {
        ProcessUtils.CommandResult branchList = ProcessUtils
                .runCommand(List.of("git", "branch", "-r"), repoDir, timeoutSeconds);
        if (branchList.exitCode != 0) {
            return false;
        }
        String out = branchList.output;
        String targetBranch;
        if (out.contains("origin/main")) {
            targetBranch = "main";
        } else if (out.contains("origin/master")) {
            targetBranch = "master";
        } else {
            return true;
        }

        ProcessUtils.CommandResult checkout = ProcessUtils
                .runCommand(List.of("git", "checkout", targetBranch), repoDir, timeoutSeconds);
        if (checkout.exitCode != 0) {
            return false;
        }
        if (isUpdate) {
            ProcessUtils.CommandResult reset = ProcessUtils
                    .runCommand(List.of("git", "reset", "--hard", "origin/" + targetBranch), repoDir, timeoutSeconds);
            return reset.exitCode == 0;
        }
        return true;
    }
}
