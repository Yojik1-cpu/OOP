import dsl.ConfigLoader;
import dsl.ScriptData;
import model.Assignment;
import model.LabTask;
import model.Student;
import model.StudentGroup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import service.grading.FinalReport;
import service.report.HtmlReportRenderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {

    @TempDir
    Path tempDir;

    @Test
    public void testFullPipelineWithMockConfig() throws IOException {
        Path configFile = tempDir.resolve("test-config.groovy");
        String content = "task { id 'T1'; title 'Test Task'; maxScore 2; softDeadline '2023-01-01'; hardDeadline '2023-01-10' }\n" +
                         "globalSettings { semesterStart '2023-01-01'; semesterEnd '2023-05-01' }\n" +
                         "group { name 'G1'; student { githubNick 'S1'; fullName 'Student 1'; repoUrl 'https://github.com/test/repo' } }\n" +
                         "assignment { studentGithubNick 'S1'; taskId 'T1'; score 2 }";
        Files.writeString(configFile, content);

        ConfigLoader loader = new ConfigLoader();
        ScriptData data = loader.load(configFile);

        assertEquals(1, data.getTasks().size());
        assertEquals(1, data.getAssignments().size());
        
        assertNotNull(data.getGlobalSettings().getSemesterStart());
        assertEquals(LocalDate.of(2023, 1, 1), data.getGlobalSettings().getSemesterStart());
    }

    @Test
    public void testHtmlGeneration() {
        ScriptData data = new ScriptData();
        LabTask task = new LabTask();
        task.setId("T1");
        task.setTitle("Task 1");
        data.getTasks().add(task);

        Student student = new Student();
        student.setGithubNick("S1");
        StudentGroup group = new StudentGroup();
        group.addStudent(student);
        data.getGroups().add(group);

        FinalReport report = new FinalReport();
        FinalReport.GradingRow row = new FinalReport.GradingRow();
        row.setStudentGithubNick("S1");
        row.setTaskId("T1");
        row.setFinalScore(1.5);
        row.setBuildOk(true);
        row.setMessage("OK");
        report.addRow(row);

        HtmlReportRenderer renderer = new HtmlReportRenderer();
        String html = renderer.render(report, data);

        assertNotNull(html);
        assertTrue(html.contains("Task 1"));
        assertTrue(html.contains("S1"));
        assertTrue(html.contains("1.5"));
    }
}
