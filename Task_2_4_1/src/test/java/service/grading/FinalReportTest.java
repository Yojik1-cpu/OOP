package service.grading;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class FinalReportTest {

    @Test
    public void testFinalReport() {
        FinalReport report = new FinalReport();
        report.setTotalAssignments(10);
        report.setSuccessfulAssignments(8);
        report.setFailedAssignments(2);
        report.setAverageScore(85.5);
        FinalReport.GradingRow row = new FinalReport.GradingRow();
        report.addRow(row);
        FinalReport.StudentSummary summary = new FinalReport.StudentSummary();
        report.addStudentSummary(summary);

        assertEquals(10, report.getTotalAssignments());
        assertEquals(8, report.getSuccessfulAssignments());
        assertEquals(2, report.getFailedAssignments());
        assertEquals(85.5, report.getAverageScore());
        assertEquals(1, report.getRows().size());
        assertEquals(1, report.getStudentSummaries().size());
    }

    @Test
    public void testGradingRow() {
        FinalReport.GradingRow row = new FinalReport.GradingRow();
        LocalDate date = LocalDate.now();
        row.setStudentGithubNick("test");
        row.setTaskId("T1");
        row.setGitOk(true);
        row.setBuildOk(false);
        row.setDocsOk(true);
        row.setStyleOk(false);
        row.setStyleErrors(5);
        row.setTestsOk(true);
        row.setTotalTests(10);
        row.setFailedTests(1);
        row.setSkippedTests(2);
        row.setPassedTests(7);
        row.setCodeCoverage(0.8);
        row.setFinalScore(9.5);
        row.setMessage("Message");
        row.setFirstCommitDate(date);
        row.setLastCommitDate(date);

        assertEquals("test", row.getStudentGithubNick());
        assertEquals("T1", row.getTaskId());
        assertTrue(row.isGitOk());
        assertFalse(row.isBuildOk());
        assertTrue(row.isDocsOk());
        assertFalse(row.isStyleOk());
        assertEquals(5, row.getStyleErrors());
        assertTrue(row.isTestsOk());
        assertEquals(10, row.getTotalTests());
        assertEquals(1, row.getFailedTests());
        assertEquals(2, row.getSkippedTests());
        assertEquals(7, row.getPassedTests());
        assertEquals(0.8, row.getCodeCoverage());
        assertEquals(9.5, row.getFinalScore());
        assertEquals("Message", row.getMessage());
        assertEquals(date, row.getFirstCommitDate());
        assertEquals(date, row.getLastCommitDate());
    }

    @Test
    public void testStudentSummary() {
        FinalReport.StudentSummary summary = new FinalReport.StudentSummary();
        summary.setGithubNick("test");
        summary.setFullName("Test User");
        summary.setTotalScore(95.0);
        summary.setMaxScore(100);
        summary.setFinalGrade("5");
        summary.setActivityPercentage(0.9);
        summary.setActiveWeeks(9);
        summary.setTotalWeeks(10);
        FinalReport.CheckpointScore cpScore = new FinalReport.CheckpointScore();
        summary.addCheckpoint(cpScore);

        assertEquals("test", summary.getGithubNick());
        assertEquals("Test User", summary.getFullName());
        assertEquals(95.0, summary.getTotalScore());
        assertEquals(100, summary.getMaxScore());
        assertEquals("5", summary.getFinalGrade());
        assertEquals(0.9, summary.getActivityPercentage());
        assertEquals(9, summary.getActiveWeeks());
        assertEquals(10, summary.getTotalWeeks());
        assertEquals(1, summary.getCheckpoints().size());
    }

    @Test
    public void testCheckpointScore() {
        FinalReport.CheckpointScore score = new FinalReport.CheckpointScore();
        score.setName("CP1");
        score.setDate("2023-10-01");
        score.setScore(80.0);
        score.setGrade("4");

        assertEquals("CP1", score.getName());
        assertEquals("2023-10-01", score.getDate());
        assertEquals(80.0, score.getScore());
        assertEquals("4", score.getGrade());
    }
}
