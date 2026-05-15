package service.build;

import model.GlobalSettings;
import service.utils.JacocoReportParser;
import service.utils.ProcessUtils;
import service.utils.ProcessUtils.CommandResult;
import service.utils.SimpleLogger;
import service.utils.TestReportParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GradleBuildTool implements BuildTool {

    @Override
    public boolean canHandle(Path projectDir) {
        return Files.exists(projectDir.resolve("build.gradle"))
                || Files.exists(projectDir.resolve("build.gradle.kts"));
    }

    @Override
    public CommandResult compile(Path projectDir, GlobalSettings settings)
            throws IOException, InterruptedException {
        long timeout = Optional.ofNullable(settings.getBuildTimeoutSeconds()).orElse(300L);
        return ProcessUtils.runCommand(resolveCommand(projectDir, "classes"), projectDir, timeout);
    }

    @Override
    public CommandResult generateDocs(Path projectDir, GlobalSettings settings)
            throws IOException, InterruptedException {
        long timeout = Optional.ofNullable(settings.getBuildTimeoutSeconds()).orElse(300L);
        return ProcessUtils.runCommand(resolveCommand(projectDir, "javadoc"), projectDir, timeout);
    }

    @Override
    public TestStats runTests(Path projectDir, GlobalSettings settings)
            throws IOException, InterruptedException {
        long testTimeout = Optional.ofNullable(settings.getTestTimeoutSeconds()).orElse(300L);
        List<String> command = new ArrayList<>(resolveCommand(projectDir, "test"));
        command.add("jacocoTestReport");
        command.add("--rerun-tasks");
        command.add("--continue");
        
        CommandResult result = ProcessUtils.runCommand(command, projectDir, testTimeout);
        if (result.exitCode != 0) {
            SimpleLogger.warn("Gradle tests or jacoco failed for project in " + projectDir + ":\n" + result.output);
        }

        Path jacocoFile = projectDir
                .resolve("build")
                .resolve("reports")
                .resolve("jacoco")
                .resolve("test")
                .resolve("jacocoTestReport.xml");
        Double coverage = JacocoReportParser.parseCoverage(jacocoFile);
        
        Path reportsDir = projectDir.resolve("build").resolve("test-results").resolve("test");
        return TestReportParser.parseXmlReports(reportsDir, result.exitCode == 0,
                result.output, coverage);
    }

    private List<String> resolveCommand(Path projectDir, String task) {
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
}
