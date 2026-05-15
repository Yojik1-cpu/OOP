package service.build;

import model.GlobalSettings;
import service.utils.ProcessUtils;
import service.utils.ProcessUtils.CommandResult;
import service.utils.SimpleLogger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavacBuildTool implements BuildTool {
    @Override
    public boolean canHandle(Path projectDir) {
        try {
            return !collectJavaFiles(projectDir).isEmpty();
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public CommandResult compile(Path projectDir, GlobalSettings settings)
            throws IOException, InterruptedException {
        long timeout = Optional.ofNullable(settings.getBuildTimeoutSeconds()).orElse(300L);
        List<String> javaFiles = collectJavaFiles(projectDir);
        if (javaFiles.isEmpty()) {
            return new CommandResult(-1, "No Java files found.");
        }
        Path outDir = projectDir.resolve("build/classes");
        Files.createDirectories(outDir);
        List<String> command = new ArrayList<>();
        command.add("javac");
        command.add("-d");
        command.add(outDir.toString());
        command.addAll(javaFiles);
        return ProcessUtils.runCommand(command, projectDir, timeout);
    }

    @Override
    public CommandResult generateDocs(Path projectDir, GlobalSettings settings)
            throws IOException, InterruptedException {
        long timeout = Optional.ofNullable(settings.getBuildTimeoutSeconds()).orElse(300L);
        List<String> javaFiles = collectJavaFiles(projectDir.resolve("src/main/java"));
        if (javaFiles.isEmpty()) {
            javaFiles = collectJavaFiles(projectDir);
        }
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
        return ProcessUtils.runCommand(command, projectDir, timeout);
    }

    @Override
    public TestStats runTests(Path projectDir, GlobalSettings settings)
            throws IOException, InterruptedException {
        SimpleLogger.warn("No build tool (Gradle/Maven) found in "
                + projectDir
                + ". Cannot run tests automatically.");
        return new TestStats(0, 0, 0, true,
                "No build tool for automated tests.", null);
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
}
