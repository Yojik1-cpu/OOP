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
import java.util.List;
import java.util.Optional;

public class MavenBuildTool implements BuildTool {

    @Override
    public boolean canHandle(Path projectDir) {
        return Files.exists(projectDir.resolve("pom.xml"));
    }

    @Override
    public CommandResult compile(Path projectDir, GlobalSettings settings)
            throws IOException, InterruptedException {
        long timeout = Optional.ofNullable(settings.getBuildTimeoutSeconds()).orElse(300L);
        return ProcessUtils.runCommand(List.of("mvn", "compile", "test-compile"), projectDir, timeout);
    }

    @Override
    public CommandResult generateDocs(Path projectDir, GlobalSettings settings)
            throws IOException, InterruptedException {
        long timeout = Optional.ofNullable(settings.getBuildTimeoutSeconds()).orElse(300L);
        return ProcessUtils.runCommand(List.of("mvn", "javadoc:javadoc"), projectDir, timeout);
    }

    @Override
    public TestStats runTests(Path projectDir, GlobalSettings settings)
            throws IOException, InterruptedException {
        long testTimeout = Optional.ofNullable(settings.getTestTimeoutSeconds()).orElse(300L);
        CommandResult result = ProcessUtils
                .runCommand(List.of("mvn", "clean", "test", "jacoco:report"), projectDir, testTimeout);
        if (result.exitCode != 0) {
            SimpleLogger.warn("Maven tests or jacoco failed for project in " + projectDir + ":\n" + result.output);
        }
        Path jacocoFile = projectDir
                .resolve("target")
                .resolve("site")
                .resolve("jacoco")
                .resolve("jacoco.xml");
        Double coverage = JacocoReportParser.parseCoverage(jacocoFile);

        Path reportsDir = projectDir.resolve("target").resolve("surefire-reports");
        return TestReportParser
                .parseXmlReports(reportsDir, result.exitCode == 0, result.output, coverage);
    }
}
