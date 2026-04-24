package service;

import dsl.ScriptData;
import model.Assignment;
import model.Checkpoint;
import model.GlobalSettings;
import model.LabTask;
import model.Student;
import model.StudentGroup;
import service.StyleCheckService.StyleResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GradingService {
    private static final Pattern GRADLE_TESTS_PATTERN =
            Pattern.compile("(\\d+) tests completed(?:, (\\d+) failed)?(?:, (\\d+) skipped)?");
    private static final Pattern MAVEN_TESTS_PATTERN =
            Pattern.compile("Tests run: (\\d+), Failures: (\\d+), Errors: (\\d+), Skipped: (\\d+)");
    private static final long DEFAULT_TEST_TIMEOUT_SECONDS = 300;

    public FinalReport executeGrading(ScriptData data) {
        verifyGitConfig();
        Map<String, Student> studentsByNick = resolveStudentsFromGroups(data);
        Map<String, LabTask> tasksById = data.getTasks().stream()
                .collect(Collectors.toMap(LabTask::getId, task -> task, (a, b) -> a));
        GlobalSettings settings = data.getGlobalSettings();

        List<AssignmentResult> results = new ArrayList<>();
        try (ExecutorService executor = java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<AssignmentResult>> futures = new ArrayList<>();
            for (Assignment assignment : data.getAssignments()) {
                futures.add(executor.submit(() -> processAssignment(assignment, studentsByNick, tasksById, settings)));
            }
            for (Future<AssignmentResult> future : futures) {
                try {
                    results.add(future.get());
                } catch (ExecutionException e) {
                    Assignment failed = new Assignment();
                    failed.setTaskId("unknown");
                    failed.setStudentGithubNick("unknown");
                    results.add(AssignmentResult.error(failed, "Unhandled execution error: " +
                            e.getCause().getMessage()));
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Grading was interrupted: " + e.getMessage(), e);
        }
        return toFinalReport(results, data, studentsByNick, tasksById, settings);
    }

    private AssignmentResult processAssignment(
            Assignment assignment,
            Map<String, Student> studentsByNick,
            Map<String, LabTask> tasksById,
            GlobalSettings settings
    ) {
        Student student = studentsByNick.get(assignment.getStudentGithubNick());
        LabTask task = tasksById.get(assignment.getTaskId());
        if (student == null || task == null) {
            return AssignmentResult.error(assignment, "Student or task was not found in config.");
        }
        if (student.getRepoUrl() == null || student.getRepoUrl().isBlank()) {
            return AssignmentResult.error(assignment, "Repository URL is missing for student.");
        }

        Path tempRoot;
        try {
            tempRoot = Files.createTempDirectory("grading-" + safeName(assignment.getStudentGithubNick()) + "-");
        } catch (IOException e) {
            return AssignmentResult.error(assignment, "Failed to create temp directory: " + e.getMessage());
        }

        Path repoDir = tempRoot.resolve("repo");
        AssignmentResult result = new AssignmentResult(assignment);
        try {
            result.gitOk = cloneRepository(student.getRepoUrl(), repoDir, settings);
            if (!result.gitOk) {
                result.error = "Git clone/update failed.";
                return result;
            }

            Path projectDir = resolveProjectDir(repoDir, assignment);
            if (projectDir == null) {
                result.error = "Project path was not found in repository: " + assignment.getProjectPath();
                return result;
            }

            CommandResult compileRes = compileProject(projectDir, settings);
            result.compilationOk = compileRes.exitCode == 0;
            if (!result.compilationOk) {
                result.styleResult = new StyleResult(false, -1,
                        "Skipped due to compilation failure");
                result.docsOk = false;
                result.testStats = new TestStats(0, 0, 0, false,
                        "Compilation failed.");
                result.finalScore = 0.0;
                result.error = "Compilation failed:\n" + compileRes.output;
                System.err.println(result.error);
                return result;
            }

            CommandResult docsRes = generateDocs(projectDir, settings);
            result.docsOk = docsRes.exitCode == 0;
            if (!result.docsOk) {
                result.styleResult = new StyleResult(false, -1,
                        "Skipped due to docs failure");
                result.testStats = new TestStats(0, 0, 0, false,
                        "Documentation generation failed.");
                result.finalScore = 0.0;
                result.error = "Docs failed:\n" + docsRes.output;
                System.err.println(result.error);
                return result;
            }

            result.styleResult = StyleCheckService.checkStyle(repoDir, projectDir);
            result.testStats = runTests(projectDir, settings);
            result.finalScore = calculateScore(task, assignment, settings);
            return result;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            result.error = "Execution interrupted: " + e.getMessage();
            return result;
        } catch (IOException e) {
            result.error = "Execution failed: " + e.getMessage();
            return result;
        }
    }

    private Path resolveProjectDir(Path repoDir, Assignment assignment) {
        if (assignment.getProjectPath() == null || assignment.getProjectPath().isBlank()) {
            return repoDir;
        }
        Path candidate = repoDir.resolve(assignment.getProjectPath()).normalize();
        return Files.isDirectory(candidate) ? candidate : null;
    }

    private void verifyGitConfig() {
        try {
            CommandResult userName = runCommand(List.of("git", "config", "--get", "user.name"),
                    null, 30);
            CommandResult helper = runCommand(List.of("git", "config", "--get", "credential.helper"),
                    null, 30);
            CommandResult askPass = runCommand(List.of("git", "config", "--get", "core.askPass"),
                    null, 30);
            if (userName.exitCode != 0 || userName.output.trim().isEmpty()) {
                throw new IllegalStateException(
                        "Git is not configured: `git config --get user.name` is empty."
                );
            }
            boolean nonInteractive = (helper.exitCode == 0 && !helper.output.trim().isEmpty())
                    || (askPass.exitCode == 0 && !askPass.output.trim().isEmpty());
            if (!nonInteractive) {
                throw new IllegalStateException(
                        "Git auth may become interactive. Configure credential.helper or core.askPass."
                );
            }
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Failed to verify git config: " + e.getMessage(), e);
        }
    }

    private boolean cloneRepository(String repoUrl, Path repoDir, GlobalSettings settings)
            throws IOException, InterruptedException {
        long timeout = Optional.ofNullable(settings.getGitTimeoutSeconds()).orElse(180L);
        CommandResult clone = runCommand(List.of("git", "clone", repoUrl, repoDir.toString()), null, timeout);
        if (clone.exitCode != 0) {
            return false;
        }
        return checkoutMainOrMaster(repoDir, timeout);
    }

    private boolean checkoutMainOrMaster(Path repoDir, long timeoutSeconds) throws IOException, InterruptedException {
        CommandResult branchList = runCommand(List.of("git", "branch", "-r"), repoDir, timeoutSeconds);
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
        CommandResult checkout = runCommand(List.of("git", "checkout", targetBranch), repoDir, timeoutSeconds);
        return checkout.exitCode == 0;
    }

    private CommandResult compileProject(Path projectDir, GlobalSettings settings)
            throws IOException, InterruptedException {
        long timeout = Optional.ofNullable(settings.getBuildTimeoutSeconds()).orElse(300L);
        if (Files.exists(projectDir.resolve("build.gradle"))
                || Files.exists(projectDir.resolve("build.gradle.kts"))) {
            return runCommand(resolveGradleCommand(projectDir, "classes"), projectDir, timeout);
        }
        List<String> javaFiles = collectJavaFiles(projectDir);
        if (javaFiles.isEmpty()) {
            return new CommandResult(-1, "No Java files found and no build.gradle present.");
        }
        Path outDir = projectDir.resolve("build/classes");
        Files.createDirectories(outDir);
        List<String> command = new ArrayList<>();
        command.add("javac");
        command.add("-d");
        command.add(outDir.toString());
        command.addAll(javaFiles);
        return runCommand(command, projectDir, timeout);
    }

    private CommandResult generateDocs(Path projectDir, GlobalSettings settings)
            throws IOException, InterruptedException {
        long timeout = Optional.ofNullable(settings.getBuildTimeoutSeconds()).orElse(300L);
        if (Files.exists(projectDir.resolve("build.gradle"))
                || Files.exists(projectDir.resolve("build.gradle.kts"))) {
            return runCommand(resolveGradleCommand(projectDir, "javadoc"), projectDir, timeout);
        }
        if (Files.exists(projectDir.resolve("pom.xml"))) {
            return runCommand(List.of("mvn", "javadoc:javadoc"), projectDir, timeout);
        }
        List<String> javaFiles = collectJavaFiles(projectDir.resolve("src/main/java"));
        if (javaFiles.isEmpty()) {
            return new CommandResult(-1, "No java files for javadoc");
        }
        Path docsDir = projectDir.resolve("build/docs");
        Files.createDirectories(docsDir);
        List<String> command = new ArrayList<>();
        command.add("javadoc");
        command.add("-d");
        command.add(docsDir.toString());
        command.addAll(javaFiles);
        return runCommand(command, projectDir, timeout);
    }

    private TestStats runTests(Path projectDir, GlobalSettings settings) throws IOException, InterruptedException {
        long testTimeout = Optional.ofNullable(settings.getTestTimeoutSeconds()).orElse(DEFAULT_TEST_TIMEOUT_SECONDS);
        CommandResult result;
        if (Files.exists(projectDir.resolve("build.gradle"))
                || Files.exists(projectDir.resolve("build.gradle.kts"))) {
            result = runCommand(resolveGradleCommand(projectDir, "test"), projectDir, testTimeout);
        } else if (Files.exists(projectDir.resolve("pom.xml"))) {
            result = runCommand(List.of("mvn", "test"), projectDir, testTimeout);
        } else {
            return new TestStats(0, 0, 0, true,
                    "No build tool for automated tests.");
        }
        if (result.exitCode != 0) {
            System.err.println("Tests failed:\n" + result.output);
        }
        return parseTestStats(result.output, result.exitCode == 0);
    }

    private double calculateScore(LabTask task, Assignment assignment, GlobalSettings settings) {
        double baseScore = assignment.getScore() != null ? assignment.getScore().doubleValue() : 0.0;

        LocalDate submittedAt = assignment.getSubmittedAt();
        if (submittedAt == null) {
            submittedAt = LocalDate.now();
        }

        if (task.getHardDeadline() != null && submittedAt.isAfter(task.getHardDeadline())) {
            baseScore -= 0.5;
        }
        if (task.getSoftDeadline() != null && submittedAt.isAfter(task.getSoftDeadline())) {
            baseScore -= 0.5;
        }

        return Math.max(0, baseScore);
    }

    private Map<String, Student> resolveStudentsFromGroups(ScriptData data) {
        Map<String, Student> map = new HashMap<>();
        for (StudentGroup group : data.getGroups()) {
            for (Student student : group.getStudents()) {
                if (student.getGithubNick() != null) {
                    map.put(student.getGithubNick(), student);
                }
            }
        }
        return map;
    }

    private CommandResult runCommand(List<String> command, Path workDir, long timeoutSeconds)
            throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(command);
        if (workDir != null) {
            pb.directory(workDir.toFile());
        }
        pb.redirectErrorStream(true);
        
        String javaHome = System.getProperty("java.home");
        pb.environment().put("JAVA_HOME", javaHome);
        pb.environment().put("GIT_TERMINAL_PROMPT", "0");
        
        Process process = pb.start();

        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
            }
        }

        boolean finished = process.waitFor(timeoutSeconds, java.util.concurrent.TimeUnit.SECONDS);
        if (!finished) {
            process.destroyForcibly();
            return new CommandResult(-1, output + "Command timed out.");
        }
        return new CommandResult(process.exitValue(), output.toString());
    }

    private List<String> resolveGradleCommand(Path projectDir, String task) {
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        Path current = projectDir;
        while (current != null) {
            Path gradlewBat = current.resolve("gradlew.bat");
            Path gradlew = current.resolve("gradlew");
            if (isWindows && Files.exists(gradlewBat)) {
                return List.of(gradlewBat.toAbsolutePath().toString(), task);
            }
            if (Files.exists(gradlew)) {
                return List.of(gradlew.toAbsolutePath().toString(), task);
            }
            current = current.getParent();
        }
        return List.of("gradle", task);
    }

    private List<String> collectJavaFiles(Path searchRoot) throws IOException {
        if (!Files.exists(searchRoot)) {
            return List.of();
        }
        try (Stream<Path> stream = Files.walk(searchRoot)) {
            return stream.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .map(Path::toString)
                    .collect(Collectors.toList());
        }
    }

    private TestStats parseTestStats(String output, boolean testCommandSuccess) {
        Matcher gradleMatcher = GRADLE_TESTS_PATTERN.matcher(output);
        if (gradleMatcher.find()) {
            int total = Integer.parseInt(gradleMatcher.group(1));
            int failed = gradleMatcher.group(2) == null ? 0 : Integer.parseInt(gradleMatcher.group(2));
            int skipped = gradleMatcher.group(3) == null ? 0 : Integer.parseInt(gradleMatcher.group(3));
            return new TestStats(total, failed, skipped, testCommandSuccess, output);
        }

        Matcher mavenMatcher = MAVEN_TESTS_PATTERN.matcher(output);
        if (mavenMatcher.find()) {
            int total = Integer.parseInt(mavenMatcher.group(1));
            int failed = Integer.parseInt(mavenMatcher.group(2)) + Integer.parseInt(mavenMatcher.group(3));
            int skipped = Integer.parseInt(mavenMatcher.group(4));
            return new TestStats(total, failed, skipped, testCommandSuccess, output);
        }

        return new TestStats(0, testCommandSuccess ? 0 : 1, 0, testCommandSuccess, output);
    }

    private String safeName(String value) {
        if (value == null || value.isBlank()) {
            return "unknown";
        }
        return value.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private FinalReport toFinalReport(
            List<AssignmentResult> results,
            ScriptData data,
            Map<String, Student> studentsByNick,
            Map<String, LabTask> tasksById,
            GlobalSettings settings
    ) {
        FinalReport report = new FinalReport();
        report.setTotalAssignments(results.size());

        int success = 0;
        double scoreSum = 0;
        for (AssignmentResult result : results) {
            if (result.error == null) {
                success++;
            }
            double score = result.finalScore == null ? 0.0 : result.finalScore;
            scoreSum += score;

            FinalReport.GradingRow row = new FinalReport.GradingRow();
            row.setStudentGithubNick(result.assignment.getStudentGithubNick());
            row.setTaskId(result.assignment.getTaskId());
            row.setGitOk(result.gitOk);
            row.setBuildOk(result.compilationOk);
            row.setDocsOk(result.docsOk);
            row.setStyleOk(result.styleResult != null && result.styleResult.passed);
            row.setStyleErrors(result.styleResult != null ? result.styleResult.errorsCount : -1);
            row.setTestsOk(result.testStats != null && result.testStats.commandSuccessful);
            row.setTotalTests(result.testStats != null ? result.testStats.total : 0);
            row.setFailedTests(result.testStats != null ? result.testStats.failed : 0);
            row.setSkippedTests(result.testStats != null ? result.testStats.skipped : 0);
            row.setPassedTests(result.testStats != null ? Math.max(0,
                    result.testStats.total - result.testStats.failed - result.testStats.skipped) : 0);
            row.setFinalScore(score);
            
            if (result.error != null) {
                row.setMessage(result.error.split("\n")[0]);
            } else if (result.styleResult != null && !result.styleResult.passed) {
                row.setMessage(result.styleResult.message.split("\n")[0]);
            }
            else {
                row.setMessage("OK");
            }
            
            report.addRow(row);
        }

        report.setSuccessfulAssignments(success);
        report.setFailedAssignments(results.size() - success);
        report.setAverageScore(results.isEmpty() ? 0.0 : scoreSum / results.size());
        fillStudentSummaries(report, results, data, studentsByNick, tasksById, settings);
        return report;
    }

    private void fillStudentSummaries(
            FinalReport report,
            List<AssignmentResult> results,
            ScriptData data,
            Map<String, Student> studentsByNick,
            Map<String, LabTask> tasksById,
            GlobalSettings settings
    ) {
        List<Checkpoint> checkpoints = data.getCheckpoints().stream()
                .sorted(Comparator.comparing(Checkpoint::getDate))
                .toList();

        for (Map.Entry<String, Student> entry : studentsByNick.entrySet()) {
            String nick = entry.getKey();
            Student student = entry.getValue();
            List<AssignmentResult> studentResults = results.stream()
                    .filter(r -> nick.equals(r.assignment.getStudentGithubNick()))
                    .toList();

            double total = studentResults.stream()
                    .mapToDouble(r -> r.finalScore == null ? 0.0 : r.finalScore).sum();
            int maxScore = studentResults.stream()
                    .map(r -> tasksById.get(r.assignment.getTaskId()))
                    .filter(java.util.Objects::nonNull)
                    .mapToInt(t -> t.getMaxScore() == null ? 100 : t.getMaxScore())
                    .sum();

            FinalReport.StudentSummary summary = new FinalReport.StudentSummary();
            summary.setGithubNick(nick);
            summary.setFullName(student.getFullName());
            summary.setTotalScore(total);
            summary.setMaxScore(maxScore);
            summary.setFinalGrade(toGrade(total, maxScore, settings));

            for (Checkpoint checkpoint : checkpoints) {
                List<AssignmentResult> checkpointAssignments = studentResults.stream()
                        .filter(r -> {
                            LabTask task = tasksById.get(r.assignment.getTaskId());
                            return task != null && isBeforeOrEqual(task.getHardDeadline(), checkpoint.getDate());
                        })
                        .toList();

                double checkpointScore = checkpointAssignments.stream()
                        .mapToDouble(r -> r.finalScore == null ? 0.0 : r.finalScore)
                        .sum();
                
                Integer requiredScore = checkpoint.getRequiredScore();
                if (requiredScore == null) {
                    requiredScore = checkpointAssignments.stream()
                        .map(r -> tasksById.get(r.assignment.getTaskId()))
                        .filter(java.util.Objects::nonNull)
                        .mapToInt(t -> t.getMaxScore() == null ? 100 : t.getMaxScore())
                        .sum();
                }

                FinalReport.CheckpointScore cp = new FinalReport.CheckpointScore();
                cp.setName(checkpoint.getName());
                cp.setDate(checkpoint.getDate() == null ? "-" : checkpoint.getDate().toString());
                cp.setScore(checkpointScore);
                cp.setGrade(toGrade(checkpointScore, requiredScore, settings));
                summary.addCheckpoint(cp);
            }
            report.addStudentSummary(summary);
        }
    }

    private static boolean isBeforeOrEqual(LocalDate date, LocalDate checkpointDate) {
        if (checkpointDate == null) {
            return true;
        }
        if (date == null) {
            return false;
        }
        return !date.isAfter(checkpointDate);
    }

    private static String toGrade(double score, double maxScore, GlobalSettings settings) {
        if (maxScore <= 0) {
            return "N/A";
        }
        double percent = (score * 100.0) / maxScore;
        
        int excellent = Optional.ofNullable(settings.getExcellentThreshold()).orElse(80);
        int good = Optional.ofNullable(settings.getGoodThreshold()).orElse(60);
        int satisfactory = Optional.ofNullable(settings.getSatisfactoryThreshold()).orElse(40);

        if (percent >= excellent) {
            return "5";
        }
        if (percent >= good) {
            return "4";
        }
        if (percent >= satisfactory) {
            return "3";
        }
        return "2";
    }

    public static class AssignmentResult {
        private final Assignment assignment;
        private boolean gitOk;
        private boolean compilationOk;
        private boolean docsOk;
        private StyleResult styleResult;
        private TestStats testStats;
        private Double finalScore;
        private String error;

        public AssignmentResult(Assignment assignment) {
            this.assignment = assignment;
        }

        public static AssignmentResult error(Assignment assignment, String message) {
            AssignmentResult result = new AssignmentResult(assignment);
            result.error = message;
            return result;
        }

        @Override
        public String toString() {
            return "AssignmentResult{" +
                    "assignment=" + assignment +
                    ", gitOk=" + gitOk +
                    ", compilationOk=" + compilationOk +
                    ", docsOk=" + docsOk +
                    ", styleResult=" + styleResult +
                    ", tests=" + String.valueOf(testStats) +
                    ", finalScore=" + finalScore +
                    ", error='" + error + '\'' +
                    '}';
        }
    }

    private static class CommandResult {
        private final int exitCode;
        private final String output;

        private CommandResult(int exitCode, String output) {
            this.exitCode = exitCode;
            this.output = output;
        }
    }

    public static class TestStats {
        private final int total;
        private final int failed;
        private final int skipped;
        private final boolean commandSuccessful;
        private final String rawOutput;

        public TestStats(int total, int failed, int skipped, boolean commandSuccessful, String rawOutput) {
            this.total = total;
            this.failed = failed;
            this.skipped = skipped;
            this.commandSuccessful = commandSuccessful;
            this.rawOutput = rawOutput;
        }

        @Override
        public String toString() {
            return "TestStats{" +
                    "total=" + total +
                    ", failed=" + failed +
                    ", skipped=" + skipped +
                    ", commandSuccessful=" + commandSuccessful +
                    ", rawOutput='" + rawOutput + '\'' +
                    '}';
        }
    }
}
