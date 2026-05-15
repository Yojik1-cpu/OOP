package service.grading;

import model.Assignment;
import model.GlobalSettings;
import model.LabTask;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScoreCalculatorTest {

    private final ScoreCalculator calculator = new ScoreCalculator();

    @Test
    public void testCalculateScore_NoPenalties() {
        LabTask task = new LabTask();
        task.setMaxScore(2);
        task.setSoftDeadline(LocalDate.now().plusDays(1));
        task.setHardDeadline(LocalDate.now().plusDays(2));

        Assignment assignment = new Assignment();
        assignment.setScore(null);
        
        LocalDate lastCommitDate = LocalDate.now();

        double score = calculator.calculateScore(task, assignment, new GlobalSettings(), lastCommitDate);
        assertEquals(2.0, score);
    }

    @Test
    public void testCalculateScore_SoftDeadlinePenalty() {
        LabTask task = new LabTask();
        task.setMaxScore(2);
        task.setSoftDeadline(LocalDate.now().minusDays(1));
        task.setHardDeadline(LocalDate.now().plusDays(2));

        Assignment assignment = new Assignment();
        
        LocalDate lastCommitDate = LocalDate.now();

        double score = calculator.calculateScore(task, assignment, new GlobalSettings(), lastCommitDate);
        assertEquals(1.5, score);
    }

    @Test
    public void testCalculateScore_HardDeadlinePenalty() {
        LabTask task = new LabTask();
        task.setMaxScore(2);
        task.setSoftDeadline(LocalDate.now().plusDays(1));
        task.setHardDeadline(LocalDate.now().minusDays(1));

        Assignment assignment = new Assignment();
        
        LocalDate lastCommitDate = LocalDate.now();

        double score = calculator.calculateScore(task, assignment, new GlobalSettings(), lastCommitDate);
        assertEquals(1.5, score);
    }

    @Test
    public void testCalculateScore_BothDeadlinesPenalty() {
        LabTask task = new LabTask();
        task.setMaxScore(2);
        task.setSoftDeadline(LocalDate.now().minusDays(2));
        task.setHardDeadline(LocalDate.now().minusDays(1));

        Assignment assignment = new Assignment();
        
        LocalDate lastCommitDate = LocalDate.now();

        double score = calculator.calculateScore(task, assignment, new GlobalSettings(), lastCommitDate);
        assertEquals(1.0, score);
    }

    @Test
    public void testCalculateScore_ManualScoreOverrides() {
        LabTask task = new LabTask();
        task.setMaxScore(2);

        Assignment assignment = new Assignment();
        assignment.setScore(1);
        
        double score = calculator.calculateScore(task, assignment, new GlobalSettings(), LocalDate.now());
        assertEquals(1.0, score);
    }
}
