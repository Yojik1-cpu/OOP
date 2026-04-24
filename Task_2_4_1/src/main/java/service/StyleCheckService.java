package service;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StyleCheckService {

    public static StyleResult checkStyle(Path repoDir, Path projectDir) {
        Path configPath = findCheckstyleConfig(repoDir);
        if (configPath == null) {
            return new StyleResult(false, 0, "Checkstyle configuration 'google_checks.xml' not found in repo.");
        }

        List<File> javaFiles = findJavaFiles(projectDir);
        if (javaFiles.isEmpty()) {
            return new StyleResult(true, 0, "No Java files found for style check.");
        }

        try {
            Configuration config = ConfigurationLoader.loadConfiguration(
                    configPath.toAbsolutePath().toString(),
                    new PropertiesExpander(System.getProperties())
            );

            Checker checker = new Checker();
            checker.setModuleClassLoader(Checker.class.getClassLoader());
            checker.configure(config);

            ErrorListener listener = new ErrorListener();
            checker.addListener(listener);

            int errors = checker.process(javaFiles);
            checker.destroy();

            boolean passed = errors == 0;
            return new StyleResult(passed, errors, listener.getSummary());
        } catch (CheckstyleException e) {
            return new StyleResult(false, -1, "Checkstyle configuration error: " + e.getMessage());
        }
    }

    private static Path findCheckstyleConfig(Path repoDir) {
        Path[] possiblePaths = {
                repoDir.resolve(".github").resolve("google_checks.xml"),
                repoDir.resolve("config").resolve("checkstyle").resolve("google_checks.xml"),
                repoDir.resolve("google_checks.xml")
        };

        for (Path p : possiblePaths) {
            if (Files.exists(p)) {
                return p;
            }
        }
        return null;
    }

    private static List<File> findJavaFiles(Path dir) {
        if (!Files.exists(dir)) {
            return List.of();
        }
        try (Stream<Path> walk = Files.walk(dir)) {
            return walk
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".java"))
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return List.of();
        }
    }

    public static class StyleResult {
        public final boolean passed;
        public final int errorsCount;
        public final String message;

        public StyleResult(boolean passed, int errorsCount, String message) {
            this.passed = passed;
            this.errorsCount = errorsCount;
            this.message = message;
        }
    }

    private static class ErrorListener implements AuditListener {
        private int errorCount = 0;
        private final StringBuilder firstFewErrors = new StringBuilder();

        @Override
        public void auditStarted(AuditEvent event) {}

        @Override
        public void auditFinished(AuditEvent event) {}

        @Override
        public void fileStarted(AuditEvent event) {}

        @Override
        public void fileFinished(AuditEvent event) {}

        @Override
        public void addError(AuditEvent event) {
            errorCount++;
            if (errorCount <= 5) {
                if (firstFewErrors.length() > 0) {
                    firstFewErrors.append("\n");
                }
                String file = event.getFileName();
                if (file != null) {
                    file = file.substring(file.lastIndexOf(File.separatorChar) + 1);
                } else {
                    file = "unknown";
                }
                firstFewErrors.append(file).append(":").append(event.getLine())
                        .append(" ").append(event.getMessage());
            }
        }

        @Override
        public void addException(AuditEvent event, Throwable throwable) {
            errorCount++;
            if (firstFewErrors.length() == 0) {
                firstFewErrors.append("Exception: ").append(throwable.getMessage());
            }
        }

        public String getSummary() {
            if (errorCount == 0) {
                return "OK";
            }
            String suffix = errorCount > 5 ? "\n... and " + (errorCount - 5) + " more" : "";
            return errorCount + " style violations found:\n" + firstFewErrors.toString() + suffix;
        }
    }
}
