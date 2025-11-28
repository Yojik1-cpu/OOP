package controlsystem.models;

import controlsystem.enums.EducationForm;
import controlsystem.enums.ControlType;
import controlsystem.enums.Grade;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StudentTest {

    @Test
    void constructorAndGetters_WorkCorrectly() {
        Student s = new Student("Иван", EducationForm.PAID);

        assertEquals("Иван", s.getName());
        assertEquals(EducationForm.PAID, s.getForm());
        assertNotNull(s.getGradebook());
    }

    @Test
    void canTransfer_WhenAlreadyBudget_ReturnsFalse() {
        Student s = new Student("Иван", EducationForm.BUDGET);

        assertFalse(s.canTransferToBudget());
    }

    @Test
    void canTransfer_WhenPaidAndGradebookAllows_ReturnsTrue() {
        Student s = new Student("Иван", EducationForm.PAID);

        // hasGoodLastTwoSessions() возвращает true
        Gradebook gb = s.getGradebook();
        gb.addRecord(new GradeRecord("МатАн", 1, ControlType.EXAM, Grade.EXCELLENT, true));
        gb.addRecord(new GradeRecord("ЛинАлг", 2, ControlType.EXAM, Grade.GOOD, true));

        assertTrue(s.canTransferToBudget());
    }

    @Test
    void canTransfer_WhenPaidButGradebookDenies_ReturnsFalse() {
        Student s = new Student("Иван", EducationForm.PAID);
        // плохая оценка мешает переводу
        Gradebook gb = s.getGradebook();
        gb.addRecord(new GradeRecord("Физика", 1, ControlType.EXAM, Grade.SATISFACTORY, true));

        assertFalse(s.canTransferToBudget());
    }

    @Test
    void transferToBudget_WhenAllowed_ChangesFormToBudget() {
        Student s = new Student("Иван", EducationForm.PAID);

        Gradebook gb = s.getGradebook();
        gb.addRecord(new GradeRecord("МатАн", 1, ControlType.EXAM, Grade.EXCELLENT, true));

        s.transferToBudget();

        assertEquals(EducationForm.BUDGET, s.getForm());
    }

    @Test
    void transferToBudget_WhenNotAllowed_KeepsOriginalForm() {
        Student s = new Student("Иван", EducationForm.PAID);

        Gradebook gb = s.getGradebook();
        gb.addRecord(new GradeRecord("МатАн", 1, ControlType.EXAM, Grade.UNSATISFACTORY, true));

        s.transferToBudget();

        assertEquals(EducationForm.PAID, s.getForm());
    }
}
