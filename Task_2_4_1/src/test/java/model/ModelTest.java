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
        assertNotNull(task.toString());
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
        assertNotNull(assignment.toString());
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
        assertNotNull(cp.toString());
    }

    @Test
    public void testStudentGroup() {
        StudentGroup group = new StudentGroup();
        group.setName("Group1");
        Student s = new Student();
        group.addStudent(s);

        assertEquals("Group1", group.getName());
        assertEquals(1, group.getStudents().size());
        assertEquals(s, group.getStudents().get(0));
    }

    @Test
    public void testGlobalSettings() {
        GlobalSettings settings = new GlobalSettings();
        settings.setTestTimeoutSeconds(100L);
        settings.setBuildTimeoutSeconds(200L);
        settings.setGitTimeoutSeconds(300L);
        settings.setExtraPoints(5);
        settings.setExcellentThreshold(90);
        settings.setGoodThreshold(70);
        settings.setSatisfactoryThreshold(50);
        settings.setSemesterStart(LocalDate.of(2023, 9, 1));
        settings.setSemesterEnd(LocalDate.of(2023, 12, 31));
        settings.setActivityWeight(0.3);

        assertEquals(100L, settings.getTestTimeoutSeconds());
        assertEquals(200L, settings.getBuildTimeoutSeconds());
        assertEquals(300L, settings.getGitTimeoutSeconds());
        assertEquals(5, settings.getExtraPoints());
        assertEquals(90, settings.getExcellentThreshold());
        assertEquals(70, settings.getGoodThreshold());
        assertEquals(50, settings.getSatisfactoryThreshold());
        assertEquals(LocalDate.of(2023, 9, 1), settings.getSemesterStart());
        assertEquals(LocalDate.of(2023, 12, 31), settings.getSemesterEnd());
        assertEquals(0.3, settings.getActivityWeight());
        assertNotNull(settings.toString());
    }
}
