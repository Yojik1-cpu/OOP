package service.grading;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FinalReportTest {

    @Test
    public void testFinalReportAndRows() {
        FinalReport report = new FinalReport();
        report.setTotalAssignments(10);
        report.setSuccessfulAssignments(8);
        report.setFailedAssignments(2);
        report.setAverageScore(4.5);

        assertEquals(10, report.getTotalAssignments());
        assertEquals(8, report.getSuccessfulAssignments());
        assertEquals(2, report.getFailedAssignments());
        assertEquals(4.5, report.getAverageScore());

        FinalReport.GradingRow row = new FinalReport.GradingRow();
        row.setStudentGithubNick("nick");
        row.setTaskId("task");
        row.setBuildOk(true);
        row.setStyleOk(false);
        row.setStyleErrors(15);
        row.setFinalScore(2.0);

        report.addRow(row);
        assertEquals(1, report.getRows().size());
        assertEquals("nick", report.getRows().get(0).getStudentGithubNick());
        assertFalse(report.getRows().get(0).isStyleOk());
        assertEquals(15, report.getRows().get(0).getStyleErrors());
    }

    @Test
    public void testStudentSummary() {
        FinalReport.StudentSummary summary = new FinalReport.StudentSummary();
        summary.setGithubNick("nick");
        summary.setTotalScore(15.5);
        summary.setFinalGrade("5");

        FinalReport.CheckpointScore cp = new FinalReport.CheckpointScore();
        cp.setName("Check1");
        cp.setScore(5.0);

        summary.addCheckpoint(cp);
        assertEquals(1, summary.getCheckpoints().size());
        assertEquals("Check1", summary.getCheckpoints().get(0).getName());
        assertEquals(5.0, summary.getCheckpoints().get(0).getScore());
    }
}
