import dsl.ConfigLoader;
import dsl.ScriptData;
import service.FinalReport;
import service.GradingService;
import service.HtmlReportRenderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        Path configPath = Path.of("config.groovy").toAbsolutePath().normalize();
        if (!Files.exists(configPath)) {
            System.err.println("Config file was not found: " + configPath);
            System.exit(1);
        }

        ConfigLoader loader = new ConfigLoader();
        ScriptData data = loader.load(configPath);

        System.out.println("Loaded config: " + configPath);
        System.out.println("Tasks: " + data.getTasks().size());
        System.out.println("Groups: " + data.getGroups().size());
        System.out.println("Assignments: " + data.getAssignments().size());
        System.out.println("Checkpoints: " + data.getCheckpoints().size());

        GradingService gradingService = new GradingService();
        FinalReport report = gradingService.executeGrading(data);
        
        HtmlReportRenderer renderer = new HtmlReportRenderer();
        String html = renderer.render(report);
        Path outputPath = Path.of("report.html");
        try {
            Files.writeString(outputPath, html);
            System.out.println("Report successfully saved to: " + outputPath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to write report to file: " + e.getMessage());
        }
    }
}
