import dsl.ConfigLoader;
import dsl.ScriptData;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import service.build.GradleBuildTool;
import service.grading.TestStats;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

public class IntegrationTest {

    private static class TestConfigLoader extends ConfigLoader {
        private final Map<String, String> resources = new HashMap<>();

        public void addResource(String path, String content) {
            resources.put(path, content);
        }

        @Override
        protected String readResource(String resourcePath) {
            return resources.get(resourcePath);
        }
    }

    @Test
    public void testFullPipeline() throws IOException {
        String content = "task { id 'T1'; title 'Test Task'; maxScore 10; hardDeadline '2025-01-01' }\n" +
                         "globalSettings { semesterStart '2024-01-01'; semesterEnd '2025-05-01' }\n" +
                         "group { name 'G1'; student { githubNick 'S1'; fullName 'Student One'; repoUrl 'url' } }\n" +
                         "assignment { studentGithubNick 'S1'; taskId 'T1' }";
        
        TestConfigLoader loader = new TestConfigLoader();
        loader.addResource("config.groovy", content);

        try (MockedConstruction<GitService> mockedGit = Mockito.mockConstruction(GitService.class, (mock, context) -> {
            Mockito.when(mock.prepareRepository(any(), any(), any())).thenAnswer(invocation -> {
                Path repoDir = invocation.getArgument(1);
                Files.createDirectories(repoDir.resolve("T1"));
                return true;
            });
            Mockito.when(mock.getCommitDates(any())).thenReturn(List.of(LocalDate.of(2024, 10, 1)));
            Mockito.when(mock.getLastCommitDate(any(), any())).thenReturn(LocalDate.of(2024, 10, 2));
            Mockito.when(mock.getFirstCommitDate(any(), any())).thenReturn(LocalDate.of(2024, 10, 1));
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

            ScriptData data = loader.load("config.groovy");

            GradingService gradingService = new GradingService();
            FinalReport report = gradingService.executeGrading(data);
            gradingService.close();

            HtmlReportRenderer renderer = new HtmlReportRenderer();
            String html = renderer.render(report, data);

            if (!html.contains("100%")) {
                assertEquals("DEBUG_HTML_OUTPUT", html);
            }

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
