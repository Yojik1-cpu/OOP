package controlsystem.models;

import controlsystem.enums.ControlType;
import controlsystem.enums.Grade;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class Gradebook {
    private final List<GradeRecord> records = new ArrayList<>();

    public void addRecord(GradeRecord record) {
        records.add(record);
    }

    public List<GradeRecord> getRecords() {
        return Collections.unmodifiableList(records);
    }

    // средний балл
    public double getCurrentGPA() {
        List<GradeRecord> numeric = records.stream()
                .filter(r -> r.getGrade().isNumeric())
                .toList();

        if (numeric.isEmpty()) {
            return 0.0;
        }

        int sum = numeric.stream()
                .mapToInt(r -> r.getGrade().getNumeric())
                .sum();

        return (double) sum / numeric.size();
    }


    public int getMaxSemesterWithExams() {
        return records.stream()
                .filter(r -> r.getControlType() == ControlType.EXAM)
                .mapToInt(GradeRecord::getSemester)
                .max()
                .orElse(0);
    }

    private List<GradeRecord> getExamRecordsForSemester(int sem) {
        return records.stream()
                .filter(r -> r.getSemester() == sem)
                .filter(r -> r.getControlType() == ControlType.EXAM)
                .collect(Collectors.toList());
    }

    // прогноз красного диплома
    public boolean canGetHonorsDiploma() {
        boolean hasBadFinals = records.stream()
                .filter(GradeRecord::isFinalForSubject)
                .anyMatch(r -> r.getGrade() == Grade.SATISFACTORY
                        || r.getGrade() == Grade.UNSATISFACTORY);
        if (hasBadFinals) {
            return false;
        }

        Optional<GradeRecord> thesis = records.stream()
                .filter(r -> r.getControlType() == ControlType.THESIS_DEFENCE)
                .findFirst();

        if (thesis.isPresent() && thesis.get().getGrade() != Grade.EXCELLENT) {
            return false;
        }
        // при прогнозе считаем оптимистичн => все отсутствующие итоговые - 5
        List<GradeRecord> finals = records.stream()
                .filter(GradeRecord::isFinalForSubject)
                .filter(r -> r.getGrade().isNumeric())
                .toList();

        long excellentNow = finals.stream()
                .filter(r -> r.getGrade() == Grade.EXCELLENT)
                .count();

        int totalFinals = finals.size();

        if (totalFinals == 0) {
            return true;
        }

        double maxPossibleExcellentShare =
                (double) excellentNow / totalFinals;

        return maxPossibleExcellentShare >= 0.75;
    }

    public boolean canGetIncScholarship(int sem) {
        List<GradeRecord> session = records.stream()
                .filter(r -> r.getSemester() == sem)
                .filter(r -> r.getControlType() == ControlType.EXAM
                        || r.getControlType() == ControlType.DIFF_CREDIT)
                .filter(r -> r.getGrade().isNumeric())
                .toList();

        if (session.isEmpty()) {
            return false;
        }

        return session.stream()
                .allMatch(r -> r.getGrade() == Grade.EXCELLENT);
    }

    public List<GradeRecord> getRecordsBySubject(String subject) {
        return records.stream()
                .filter(r -> r.getSubject().equalsIgnoreCase(subject))
                .toList();
    }

    public boolean hasGoodLastTwoSessions() {
        int lastSemester = getMaxSemesterWithExams();
        if (lastSemester == 0) {
            return false;
        }

        int prevSemester = lastSemester - 1;

        return noBadExamsInSemester(lastSemester)
                && (prevSemester <= 0
                || noBadExamsInSemester(prevSemester));
    }

    private boolean noBadExamsInSemester(int sem) {
        return getExamRecordsForSemester(sem).stream()
                .noneMatch(r ->
                        r.getGrade() == Grade.SATISFACTORY ||
                                r.getGrade() == Grade.UNSATISFACTORY
                );
    }

}
