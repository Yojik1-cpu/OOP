import dsl.ConfigLoader;
import dsl.ScriptData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import service.build.GradleBuildTool;
import service.build.TestStats;
import service.git.GitService;
import service.grading.FinalReport;
import service.grading.GradingService;
import service.report.HtmlReportRenderer;
import service.style.StyleCheckService;
import service.utils.ProcessUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

public class IntegrationTest {

    @TempDir
    Path tempDir;

    @Test
    public void testFullPipeline() throws IOException {
        Path configFile = tempDir.resolve("config.groovy");
        String content = "task { id 'T1'; title 'Test Task'; maxScore 10; hardDeadline '2025-01-01' }\n" +
                         "globalSettings { semesterStart '2024-01-01'; semesterEnd '2025-05-01' }\n" +
                         "group { name 'G1'; student { githubNick 'S1'; fullName 'Student One'; repoUrl 'url' } }\n" +
                         "assignment { studentGithubNick 'S1'; taskId 'T1' }";
        Files.writeString(configFile, content);

        try (MockedConstruction<GitService> mockedGit = Mockito.mockConstruction(GitService.class, (mock, context) -> {
            Mockito.when(mock.cloneRepository(any(), any(), any())).thenAnswer(invocation -> {
                Path repoDir = invocation.getArgument(1);
                Files.createDirectories(repoDir.resolve("T1"));
                return true;
            });
            Mockito.when(mock.getCommitDates(any())).thenReturn(List.of(LocalDate.of(2024, 10, 1)));
            Mockito.when(mock.getLastCommitDate(any(), any())).thenReturn(LocalDate.of(2024, 10, 2));
        });
             MockedConstruction<GradleBuildTool> mockedGradle = Mockito.mockConstruction(GradleBuildTool.class, (mock, context) -> {
                 Mockito.when(mock.canHandle(any())).thenReturn(true);
                 Mockito.when(mock.compile(any(), any())).thenReturn(new ProcessUtils.CommandResult(0, ""));
                 Mockito.when(mock.generateDocs(any(), any())).thenReturn(new ProcessUtils.CommandResult(0, ""));
                 Mockito.when(mock.runTests(any(), any())).thenReturn(new TestStats(10, 0, 0, true, "", 1.0));
             });
             MockedStatic<StyleCheckService> mockedStyle = Mockito.mockStatic(StyleCheckService.class)) {

            mockedStyle.when(() -> StyleCheckService.checkStyle(any(), any()))
                    .thenReturn(new StyleCheckService.StyleResult(true, 0, "OK"));

            ConfigLoader loader = new ConfigLoader();
            ScriptData data = loader.load(configFile);

            GradingService gradingService = new GradingService();
            FinalReport report = gradingService.executeGrading(data);
            gradingService.close();

            HtmlReportRenderer renderer = new HtmlReportRenderer();
            String html = renderer.render(report, data);

            assertTrue(html.contains("Student One"));
            assertTrue(html.contains("S1"));
            assertTrue(html.contains("Test Task"));
            assertTrue(html.contains("true-cell"));
            assertTrue(html.contains("<td class=\"test-col\">10</td>"));
            assertTrue(html.contains("100%"));
            assertTrue(html.contains("10.0"));
        }
    }
}
