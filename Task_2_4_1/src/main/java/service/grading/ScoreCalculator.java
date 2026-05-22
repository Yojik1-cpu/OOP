package service.grading;

import model.Assignment;
import model.GlobalSettings;
import model.LabTask;

import java.time.LocalDate;

public class ScoreCalculator {
    public double calculateScore(LabTask task, Assignment assignment,
                                 GlobalSettings settings, LocalDate firstCommitDate, LocalDate lastCommitDate) {
        double baseScore;
        if (assignment.getScore() != null) {
            baseScore = assignment.getScore().doubleValue();
        } else {
            baseScore = task.getMaxScore() != null ? task.getMaxScore().doubleValue() : 100.0;
        }

        LocalDate submittedAtForSoft = assignment.getSubmittedAt();
        if (submittedAtForSoft == null) {
            submittedAtForSoft = firstCommitDate;
        }
        if (submittedAtForSoft == null) {
            submittedAtForSoft = LocalDate.now();
        }

        LocalDate submittedAtForHard = assignment.getSubmittedAt();
        if (submittedAtForHard == null) {
            submittedAtForHard = lastCommitDate;
        }
        if (submittedAtForHard == null) {
            submittedAtForHard = LocalDate.now();
        }

        if (task.getHardDeadline() != null && submittedAtForHard.isAfter(task.getHardDeadline())) {
            baseScore -= 0.5;
        }
        if (task.getSoftDeadline() != null && submittedAtForSoft.isAfter(task.getSoftDeadline())) {
            baseScore -= 0.5;
        }
        return Math.max(0, baseScore);
    }
}
