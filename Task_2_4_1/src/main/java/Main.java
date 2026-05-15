import dsl.ConfigLoader;
import dsl.ScriptData;
import service.grading.FinalReport;
import service.grading.GradingService;
import service.report.HtmlReportRenderer;
import service.utils.SimpleLogger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        Path configPath = Path.of("config.groovy").toAbsolutePath().normalize();
        if (!Files.exists(configPath)) {
            SimpleLogger.error("Config file was not found: " + configPath);
            System.exit(1);
        }

        ConfigLoader loader = new ConfigLoader();
        ScriptData data = loader.load(configPath);

        SimpleLogger.info("Loaded config: " + configPath);
        SimpleLogger.info("Tasks: " + data.getTasks().size());
        SimpleLogger.info("Groups: " + data.getGroups().size());
        SimpleLogger.info("Assignments: " + data.getAssignments().size());
        SimpleLogger.info("Checkpoints: " + data.getCheckpoints().size());

        try (GradingService gradingService = new GradingService()) {
            FinalReport report = gradingService.executeGrading(data);
            
            HtmlReportRenderer renderer = new HtmlReportRenderer();
            String html = renderer.render(report, data);

            Path outputPath = Path.of("report.html");
            try {
                Files.writeString(outputPath, html);
                SimpleLogger.info("Report successfully saved to: " + outputPath.toAbsolutePath());
            } catch (IOException e) {
                SimpleLogger.error("Failed to write report to file: " + e.getMessage());
            }
        }
    }
}
