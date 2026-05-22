package service.report;

import dsl.ScriptData;
import model.LabTask;
import model.Student;
import model.StudentGroup;
import service.grading.FinalReport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HtmlReportRendererTest {

    private final HtmlReportRenderer renderer = new HtmlReportRenderer();

    private ScriptData createTestData() {
        ScriptData data = new ScriptData();
        LabTask task = new LabTask();
        task.setId("Task_1");
        task.setTitle("Test Task");
        data.getTasks().add(task);

        Student student = new Student();
        student.setGithubNick("test-student");
        student.setFullName("Test Student");
        StudentGroup group = new StudentGroup();
        group.addStudent(student);
        data.getGroups().add(group);
        return data;
    }

    @Test
    public void testRender_ContainsMainElements() {
        FinalReport report = new FinalReport();
        FinalReport.GradingRow row = new FinalReport.GradingRow();
        row.setStudentGithubNick("test-student");
        row.setTaskId("Task_1");
        row.setBuildOk(true);
        row.setFinalScore(1.2345);
        row.setMessage("OK");
        report.addRow(row);

        ScriptData data = createTestData();
        String html = renderer.render(report, data);

        assertTrue(html.contains("OOP Grading Report"));
        assertTrue(html.contains("test-student"));
        assertTrue(html.contains("Task: Test Task"));
        assertTrue(html.contains("true-cell"));
        assertTrue(html.contains("1.2")); 
    }

    @Test
    public void testRender_CellStatuses() {
        FinalReport report = new FinalReport();
        FinalReport.GradingRow row = new FinalReport.GradingRow();
        row.setStudentGithubNick("test-student");
        row.setTaskId("Task_1");
        row.setBuildOk(false);
        row.setCodeCoverage(null);
        report.addRow(row);

        ScriptData data = createTestData();
        String html = renderer.render(report, data);

        assertTrue(html.contains("false-cell"));
        assertTrue(html.contains("dash-cell"));
    }

    @Test
    public void testRender_WithSummariesAndCheckpoints() {
        FinalReport report = new FinalReport();
        FinalReport.StudentSummary summary = new FinalReport.StudentSummary();
        summary.setGithubNick("test-student");
        summary.setFullName("Test Student");
        summary.setTotalScore(95.5);
        summary.setFinalGrade("5");

        FinalReport.CheckpointScore cp = new FinalReport.CheckpointScore();
        cp.setName("CP1");
        cp.setScore(80.0);
        cp.setGrade("4");
        cp.setDate("2023-10-20");
        summary.addCheckpoint(cp);
        report.addStudentSummary(summary);

        ScriptData data = createTestData();
        String html = renderer.render(report, data);

        assertTrue(html.contains("Student Summaries") || html.contains("Checkpoint and Final Grades"));
        assertTrue(html.contains("test-student (Test Student)"));
        assertTrue(html.contains("95.5"));
        assertTrue(html.contains("CP1"));
        assertTrue(html.contains("2023-10-20"));
        assertTrue(html.contains("80.0"));
        assertTrue(html.contains("/ 4"));
    }
}
