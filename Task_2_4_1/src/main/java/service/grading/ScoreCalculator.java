package service.grading;

import model.Assignment;
import model.GlobalSettings;
import model.LabTask;

import java.time.LocalDate;

public class ScoreCalculator {
    public double calculateScore(LabTask task, Assignment assignment,
                                 GlobalSettings settings, LocalDate lastCommitDate) {
        double baseScore;
        if (assignment.getScore() != null) {
            baseScore = assignment.getScore().doubleValue();
        } else {
            baseScore = task.getMaxScore() != null ? task.getMaxScore().doubleValue() : 100.0;
        }

        LocalDate submittedAt = assignment.getSubmittedAt();
        if (submittedAt == null) {
            submittedAt = lastCommitDate;
        }
        if (submittedAt == null) {
            submittedAt = LocalDate.now();
        }

        if (task.getHardDeadline() != null && submittedAt.isAfter(task.getHardDeadline())) {
            baseScore -= 0.5;
        }
        if (task.getSoftDeadline() != null && submittedAt.isAfter(task.getSoftDeadline())) {
            baseScore -= 0.5;
        }
        return Math.max(0, baseScore);
    }
}
