package model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class ModelTest {

    @Test
    public void testLabTask() {
        LabTask task = new LabTask();
        task.setId("T1");
        task.setTitle("Title");
        task.setMaxScore(10);
        task.setSoftDeadline(LocalDate.of(2023, 1, 1));
        task.setHardDeadline(LocalDate.of(2023, 1, 10));

        assertEquals("T1", task.getId());
        assertEquals("Title", task.getTitle());
        assertEquals(10, task.getMaxScore());
        assertEquals(LocalDate.of(2023, 1, 1), task.getSoftDeadline());
        assertEquals(LocalDate.of(2023, 1, 10), task.getHardDeadline());

        String taskString = task.toString();
        assertTrue(taskString.contains("T1"));
        assertTrue(taskString.contains("Title"));
        assertTrue(taskString.contains("10"));
        assertTrue(taskString.contains("2023-01-01"));
        assertTrue(taskString.contains("2023-01-10"));
    }

    @Test
    public void testStudent() {
        Student student = new Student();
        student.setGithubNick("nick");
        student.setFullName("Full Name");
        student.setRepoUrl("url");

        assertEquals("nick", student.getGithubNick());
        assertEquals("Full Name", student.getFullName());
        assertEquals("url", student.getRepoUrl());

        String studentString = student.toString();
        assertTrue(studentString.contains("nick"));
        assertTrue(studentString.contains("Full Name"));
        assertTrue(studentString.contains("url"));
    }

    @Test
    public void testAssignment() {
        Assignment assignment = new Assignment();
        assignment.setStudentGithubNick("nick");
        assignment.setTaskId("T1");
        assignment.setProjectPath("path");
        assignment.setScore(5);
        assignment.setSubmittedAt(LocalDate.of(2023, 1, 5));

        assertEquals("nick", assignment.getStudentGithubNick());
        assertEquals("T1", assignment.getTaskId());
        assertEquals("path", assignment.getProjectPath());
        assertEquals(5, assignment.getScore());
        assertEquals(LocalDate.of(2023, 1, 5), assignment.getSubmittedAt());

        String assignmentString = assignment.toString();
        assertTrue(assignmentString.contains("nick"));
        assertTrue(assignmentString.contains("T1"));
        assertTrue(assignmentString.contains("path"));
        assertTrue(assignmentString.contains("5"));
        assertTrue(assignmentString.contains("2023-01-05"));
    }

    @Test
    public void testCheckpoint() {
        Checkpoint cp = new Checkpoint();
        cp.setName("CP1");
        cp.setDate(LocalDate.of(2023, 2, 1));
        cp.setRequiredScore(50);

        assertEquals("CP1", cp.getName());
        assertEquals(LocalDate.of(2023, 2, 1), cp.getDate());
        assertEquals(50, cp.getRequiredScore());

        String cpString = cp.toString();
        assertTrue(cpString.contains("CP1"));
        assertTrue(cpString.contains("2023-02-01"));
        assertTrue(cpString.contains("50"));
    }

    @Test
    public void testStudentGroup() {
        StudentGroup group = new StudentGroup();
        group.setName("Group1");
        Student s = new Student();
        s.setGithubNick("student-nick");
        group.addStudent(s);

        assertEquals("Group1", group.getName());
        assertEquals(1, group.getStudents().size());
        assertEquals(s, group.getStudents().get(0));

        String groupString = group.toString();
        assertTrue(groupString.contains("Group1"));
        assertTrue(groupString.contains("student-nick"));
    }

    @Test
    public void testGlobalSettings() {
        GlobalSettings settings = new GlobalSettings();

        assertEquals(300L, settings.getTestTimeoutSeconds());
        assertEquals(300L, settings.getBuildTimeoutSeconds());
        assertEquals(180L, settings.getGitTimeoutSeconds());
        assertEquals(0, settings.getExtraPoints());
        assertEquals(80, settings.getExcellentThreshold());
        assertEquals(60, settings.getGoodThreshold());
        assertEquals(40, settings.getSatisfactoryThreshold());
        assertEquals(0.2, settings.getActivityWeight());
        assertNull(settings.getSemesterStart());
        assertNull(settings.getSemesterEnd());

        settings.setTestTimeoutSeconds(100L);
        settings.setBuildTimeoutSeconds(200L);
        settings.setGitTimeoutSeconds(150L);
        settings.setExtraPoints(5);
        settings.setExcellentThreshold(85);
        settings.setGoodThreshold(65);
        settings.setSatisfactoryThreshold(45);
        settings.setActivityWeight(0.5);
        settings.setSemesterStart(LocalDate.of(2023, 9, 1));
        settings.setSemesterEnd(LocalDate.of(2023, 12, 31));

        assertEquals(100L, settings.getTestTimeoutSeconds());
        assertEquals(200L, settings.getBuildTimeoutSeconds());
        assertEquals(150L, settings.getGitTimeoutSeconds());
        assertEquals(5, settings.getExtraPoints());
        assertEquals(85, settings.getExcellentThreshold());
        assertEquals(65, settings.getGoodThreshold());
        assertEquals(45, settings.getSatisfactoryThreshold());
        assertEquals(0.5, settings.getActivityWeight());
        assertEquals(LocalDate.of(2023, 9, 1), settings.getSemesterStart());
        assertEquals(LocalDate.of(2023, 12, 31), settings.getSemesterEnd());

        String settingsString = settings.toString();
        assertTrue(settingsString.contains("100"));
        assertTrue(settingsString.contains("200"));
        assertTrue(settingsString.contains("150"));
        assertTrue(settingsString.contains("5"));
        assertTrue(settingsString.contains("85"));
        assertTrue(settingsString.contains("65"));
        assertTrue(settingsString.contains("45"));
        assertTrue(settingsString.contains("0.5"));
        assertTrue(settingsString.contains("2023-09-01"));
        assertTrue(settingsString.contains("2023-12-31"));
    }
}
