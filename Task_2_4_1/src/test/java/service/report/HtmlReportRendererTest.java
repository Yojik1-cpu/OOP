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

    @Test
    public void testRender_ContainsMainElements() {
        FinalReport report = new FinalReport();
        report.setTotalAssignments(1);
        report.setSuccessfulAssignments(1);
        
        FinalReport.GradingRow row = new FinalReport.GradingRow();
        row.setStudentGithubNick("test-student");
        row.setTaskId("Task_1");
        row.setBuildOk(true);
        row.setFinalScore(1.0);
        row.setMessage("OK");
        report.addRow(row);

        ScriptData data = new ScriptData();
        LabTask task = new LabTask();
        task.setId("Task_1");
        task.setTitle("Test Task");
        data.getTasks().add(task);
        
        Student student = new Student();
        student.setGithubNick("test-student");
        StudentGroup group = new StudentGroup();
        group.addStudent(student);
        data.getGroups().add(group);

        String html = renderer.render(report, data);

        assertTrue(html.contains("OOP Grading Report"));
        assertTrue(html.contains("test-student"));
        assertTrue(html.contains("Task: Test Task"));
        assertTrue(html.contains("true-cell"));
    }
}
