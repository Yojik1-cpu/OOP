package controlsystem.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import controlsystem.enums.ControlType;
import controlsystem.enums.Grade;
import java.util.List;
import org.junit.jupiter.api.Test;

class GradebookTest {

    @Test
    void currentGpa_WhenNoRecords_ReturnsZero() {
        Gradebook gb = new Gradebook();

        double gpa = gb.getCurrentGPA();

        assertEquals(0.0, gpa, 1e-6);
    }

    @Test
    void currentGpa_IgnoresNonNumericGrades() {
        Gradebook gb = new Gradebook();
        gb.addRecord(new GradeRecord("Зачётка", 1,
                ControlType.CREDIT, Grade.PASS, true));
        gb.addRecord(new GradeRecord("Практика", 1,
                ControlType.PRACTICE_DEFENCE, Grade.PASS, true));

        double gpa = gb.getCurrentGPA();

        assertEquals(0.0, gpa, 1e-6);
    }

    @Test
    void currentGpa_WithMixedGrades_ComputesAverageOfNumericOnly() {
        Gradebook gb = new Gradebook();
        gb.addRecord(new GradeRecord("Математика", 1, ControlType.EXAM, Grade.EXCELLENT, true));
        gb.addRecord(new GradeRecord("Физика", 1, ControlType.EXAM, Grade.GOOD, true));
        gb.addRecord(new GradeRecord("Зачёт", 1, ControlType.CREDIT, Grade.PASS, false));

        double gpa = gb.getCurrentGPA();

        assertEquals(4.5, gpa, 1e-6);
    }

    @Test
    void getRecords_ReturnsUnmodifiableCopy() {
        Gradebook gb = new Gradebook();
        gb.addRecord(new GradeRecord("Математика", 1, ControlType.EXAM, Grade.EXCELLENT, true));

        List<GradeRecord> records = gb.getRecords();

        assertEquals(1, records.size());
        assertThrows(UnsupportedOperationException.class, () -> records.add(
                new GradeRecord("Физика", 1, ControlType.EXAM, Grade.GOOD, true)
        ));
    }


    @Test
    void maxSemesterWithExams_WhenNoExams_ReturnsZero() {
        Gradebook gb = new Gradebook();
        gb.addRecord(new GradeRecord("Зачёт", 1, ControlType.CREDIT, Grade.PASS, true));

        assertEquals(0, gb.getMaxSemesterWithExams());
    }

    @Test
    void maxSemesterWithExams_ReturnsHighestSemesterWithExam() {
        Gradebook gb = new Gradebook();
        gb.addRecord(new GradeRecord("Математика", 1, ControlType.EXAM, Grade.EXCELLENT, true));
        gb.addRecord(new GradeRecord("Физика", 3, ControlType.EXAM, Grade.GOOD, true));
        gb.addRecord(new GradeRecord("Инфа", 2, ControlType.EXAM, Grade.EXCELLENT, true));

        assertEquals(3, gb.getMaxSemesterWithExams());
    }


    @Test
    void honorsDiploma_WhenNoFinalsYet_ReturnsTrue() {
        Gradebook gb = new Gradebook();
        // какие-то не финальные оценки
        gb.addRecord(new GradeRecord("Математика", 1, ControlType.EXAM, Grade.EXCELLENT, false));

        assertTrue(gb.canGetHonorsDiploma());
    }

    @Test
    void honorsDiploma_WhenAnyFinalIsSatisfactory_ReturnsFalse() {
        Gradebook gb = new Gradebook();
        gb.addRecord(new GradeRecord("МатАн", 1, ControlType.EXAM, Grade.SATISFACTORY, true));
        gb.addRecord(new GradeRecord("ЛинАлг", 1, ControlType.EXAM, Grade.EXCELLENT, true));

        assertFalse(gb.canGetHonorsDiploma());
    }

    @Test
    void honorsDiploma_WhenAnyFinalIsUnsatisfactory_ReturnsFalse() {
        Gradebook gb = new Gradebook();
        gb.addRecord(new GradeRecord("МатАн", 1, ControlType.EXAM, Grade.UNSATISFACTORY, true));

        assertFalse(gb.canGetHonorsDiploma());
    }

    @Test
    void honorsDiploma_WhenThesisExistsAndNotExcellent_ReturnsFalse() {
        Gradebook gb = new Gradebook();
        // финальные оценки без троек
        gb.addRecord(new GradeRecord("МатАн", 1, ControlType.EXAM, Grade.EXCELLENT, true));
        gb.addRecord(new GradeRecord("ЛинАлг", 1, ControlType.EXAM, Grade.GOOD, true));
        // ВКР на 4
        gb.addRecord(new GradeRecord("ВКР", 8, ControlType.THESIS_DEFENCE, Grade.GOOD, true));

        assertFalse(gb.canGetHonorsDiploma());
    }

    @Test
    void honorsDiploma_WhenAtLeast75PercentFinalsAreExcellent_ReturnsTrue() {
        Gradebook gb = new Gradebook();
        // 3 из 4 финалов - отлично → 75%
        gb.addRecord(new GradeRecord("МатАн", 1, ControlType.EXAM, Grade.EXCELLENT, true));
        gb.addRecord(new GradeRecord("ЛинАлг", 1, ControlType.EXAM, Grade.EXCELLENT, true));
        gb.addRecord(new GradeRecord("Физика", 2, ControlType.EXAM, Grade.EXCELLENT, true));
        gb.addRecord(new GradeRecord("История", 2, ControlType.EXAM, Grade.GOOD, true));
        // ВКР 5
        gb.addRecord(new GradeRecord("ВКР", 8, ControlType.THESIS_DEFENCE, Grade.EXCELLENT, true));

        assertTrue(gb.canGetHonorsDiploma());
    }

    @Test
    void honorsDiploma_WhenLessThan75PercentFinalsAreExcellent_ReturnsFalse() {
        Gradebook gb = new Gradebook();
        // 2 из 4 финалов - отлично → 50%
        gb.addRecord(new GradeRecord("МатАн", 1, ControlType.EXAM, Grade.EXCELLENT, true));
        gb.addRecord(new GradeRecord("ЛинАлг", 1, ControlType.EXAM, Grade.GOOD, true));
        gb.addRecord(new GradeRecord("Физика", 2, ControlType.EXAM, Grade.GOOD, true));
        gb.addRecord(new GradeRecord("История", 2, ControlType.EXAM, Grade.EXCELLENT, true));

        assertFalse(gb.canGetHonorsDiploma());
    }

    @Test
    void incScholarship_WhenNoSessionRecords_ReturnsFalse() {
        Gradebook gb = new Gradebook();

        assertFalse(gb.canGetIncScholarship(1));
    }

    @Test
    void incScholarship_WhenAllExamsAndDiffCreditsExcellent_ReturnsTrue() {
        Gradebook gb = new Gradebook();
        gb.addRecord(new GradeRecord("МатАн", 1, ControlType.EXAM, Grade.EXCELLENT, true));
        gb.addRecord(new GradeRecord("ЛинАлг", 1, ControlType.DIFF_CREDIT, Grade.EXCELLENT, true));

        assertTrue(gb.canGetIncScholarship(1));
    }

    @Test
    void incScholarship_WhenOneExamIsNotExcellent_ReturnsFalse() {
        Gradebook gb = new Gradebook();
        gb.addRecord(new GradeRecord("МатАн", 1, ControlType.EXAM, Grade.EXCELLENT, true));
        gb.addRecord(new GradeRecord("ЛинАл", 1, ControlType.EXAM, Grade.GOOD, true));

        assertFalse(gb.canGetIncScholarship(1));
    }

    @Test
    void incScholarship_IgnoresNonNumericGradesAndStillWorks() {
        Gradebook gb = new Gradebook();
        gb.addRecord(new GradeRecord("Физра", 1, ControlType.CREDIT, Grade.PASS, false));
        gb.addRecord(new GradeRecord("МатАн", 1, ControlType.EXAM, Grade.EXCELLENT, true));

        // только один экзамен на 5 => ок
        assertTrue(gb.canGetIncScholarship(1));
    }

    @Test
    void recordsBySubject_WhenNoSuchSubject_ReturnsEmptyList() {
        Gradebook gb = new Gradebook();
        gb.addRecord(new GradeRecord("Математика", 1, ControlType.EXAM, Grade.EXCELLENT, true));

        List<GradeRecord> records = gb.getRecordsBySubject("Физика");

        assertTrue(records.isEmpty());
    }

    @Test
    void recordsBySubject_IsCaseInsensitive() {
        Gradebook gb = new Gradebook();
        gb.addRecord(new GradeRecord("Математика", 1, ControlType.EXAM, Grade.EXCELLENT, true));
        gb.addRecord(new GradeRecord("математика", 2, ControlType.EXAM, Grade.GOOD, true));
        gb.addRecord(new GradeRecord("Физика", 1, ControlType.EXAM, Grade.EXCELLENT, true));

        List<GradeRecord> records = gb.getRecordsBySubject("МАТЕМАТИКА");

        assertEquals(2, records.size());
        assertTrue(records.stream().allMatch(r -> r.getSubject().toLowerCase().contains("мат")));
    }


    @Test
    void hasGoodLastTwoSessions_WhenNoExams_ReturnsFalse() {
        Gradebook gb = new Gradebook();
        gb.addRecord(new GradeRecord("Зачёт", 1, ControlType.CREDIT, Grade.PASS, true));

        assertFalse(gb.hasGoodLastTwoSessions());
    }

    @Test
    void hasGoodLastTwoSessions_WhenOnlyOneSemesterOfExamsAndAllGood_ReturnsTrue() {
        Gradebook gb = new Gradebook();
        gb.addRecord(new GradeRecord("МатАн", 1, ControlType.EXAM, Grade.EXCELLENT, true));
        gb.addRecord(new GradeRecord("ЛинАлг", 1, ControlType.EXAM, Grade.GOOD, true));

        assertTrue(gb.hasGoodLastTwoSessions());
    }

    @Test
    void hasGoodLastTwoSessions_WhenLastTwoSemestersHaveNoBadExams_ReturnsTrue() {
        Gradebook gb = new Gradebook();
        // семестр 1 — как угодно, не считается в последних двух
        gb.addRecord(new GradeRecord("История", 1, ControlType.EXAM, Grade.SATISFACTORY, true));

        // семестр 2 — только 4 и 5
        gb.addRecord(new GradeRecord("МатАн", 2, ControlType.EXAM, Grade.EXCELLENT, true));
        gb.addRecord(new GradeRecord("ЛинАлг", 2, ControlType.EXAM, Grade.GOOD, true));

        // семестр 3 — только 4 и 5
        gb.addRecord(new GradeRecord("Физика", 3, ControlType.EXAM, Grade.EXCELLENT, true));
        gb.addRecord(new GradeRecord("Инфа", 3, ControlType.EXAM, Grade.GOOD, true));

        assertTrue(gb.hasGoodLastTwoSessions());
    }

    @Test
    void hasGoodLastTwoSessions_WhenLastSemesterHasSatisfactory_ReturnsFalse() {
        Gradebook gb = new Gradebook();
        // семестр 2 — норм
        gb.addRecord(new GradeRecord("МатАн", 2, ControlType.EXAM, Grade.EXCELLENT, true));
        // семестр 3 — есть 3
        gb.addRecord(new GradeRecord("Физика", 3, ControlType.EXAM, Grade.SATISFACTORY, true));

        assertFalse(gb.hasGoodLastTwoSessions());
    }

    @Test
    void hasGoodLastTwoSessions_WhenPreviousSemesterHasSatisfactory_ReturnsFalse() {
        Gradebook gb = new Gradebook();
        // семестр 2 — есть 3
        gb.addRecord(new GradeRecord("МатАн", 2, ControlType.EXAM, Grade.SATISFACTORY, true));
        // семестр 3 — норм
        gb.addRecord(new GradeRecord("Физика", 3, ControlType.EXAM, Grade.EXCELLENT, true));

        assertFalse(gb.hasGoodLastTwoSessions());
    }
}
