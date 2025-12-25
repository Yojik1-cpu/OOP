package controlsystem.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import controlsystem.enums.ControlType;
import controlsystem.enums.Grade;
import org.junit.jupiter.api.Test;

class GradeRecordTest {

    @Test
    void constructorAndGetters_WorkCorrectly() {
        GradeRecord record = new GradeRecord(
                "Математика",
                3,
                ControlType.EXAM,
                Grade.EXCELLENT,
                true
        );

        assertEquals("Математика", record.getSubject());
        assertEquals(3, record.getSemester());
        assertEquals(ControlType.EXAM, record.getControlType());
        assertEquals(Grade.EXCELLENT, record.getGrade());
        assertTrue(record.isFinalForSubject());
    }

    @Test
    void getters_WithDifferentValues_WorkCorrectly() {
        GradeRecord record = new GradeRecord(
                "Физика",
                1,
                ControlType.CREDIT,
                Grade.PASS,
                false
        );

        assertEquals("Физика", record.getSubject());
        assertEquals(1, record.getSemester());
        assertEquals(ControlType.CREDIT, record.getControlType());
        assertEquals(Grade.PASS, record.getGrade());
        assertFalse(record.isFinalForSubject());
    }
}
