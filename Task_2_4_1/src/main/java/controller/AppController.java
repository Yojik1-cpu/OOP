package controller;

import dsl.ConfigLoader;
import dsl.ScriptData;
import service.grading.FinalReport;
import service.grading.GradingService;
import service.report.HtmlReportRenderer;
import service.utils.SimpleLogger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AppController {

    public void run() {
        ConfigLoader loader = new ConfigLoader();
        ScriptData data;
        try {
            data = loader.load("config.groovy");
        } catch (IllegalArgumentException e) {
            SimpleLogger.error(e.getMessage());
            System.exit(1);
            return;
        }

        SimpleLogger.info("Loaded config from resources");
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
