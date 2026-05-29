package dsl;

import groovy.lang.Closure;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

public class ConfigDslContextTest {

    @Test
    public void testTaskDsl() {
        ScriptData data = new ScriptData();
        ConfigDslContext context = new ConfigDslContext(data, null);
        
        context.task(new Closure<Void>(null) {
            public void doCall() {
                ConfigDslContext.TaskDsl dsl = (ConfigDslContext.TaskDsl) getDelegate();
                dsl.id("T1");
                dsl.title("Test Task");
                dsl.maxScore(10);
                dsl.softDeadline("2023-01-01");
                dsl.hardDeadline("2023-01-10");
            }
        });

        assertEquals(1, data.getTasks().size());
        assertEquals("T1", data.getTasks().get(0).getId());
        assertEquals("Test Task", data.getTasks().get(0).getTitle());
        assertEquals(10, data.getTasks().get(0).getMaxScore());
        assertEquals(LocalDate.of(2023, 1, 1), data.getTasks().get(0).getSoftDeadline());
        assertEquals(LocalDate.of(2023, 1, 10), data.getTasks().get(0).getHardDeadline());
    }

    @Test
    public void testGroupDsl() {
        ScriptData data = new ScriptData();
        ConfigDslContext context = new ConfigDslContext(data, null);

        context.group(new Closure<Void>(null) {
            public void doCall() {
                ConfigDslContext.GroupDsl dsl = (ConfigDslContext.GroupDsl) getDelegate();
                dsl.name("G1");
                dsl.student(new Closure<Void>(null) {
                    public void doCall() {
                        ConfigDslContext.StudentDsl studentDsl = (ConfigDslContext.StudentDsl) getDelegate();
                        studentDsl.githubNick("S1");
                        studentDsl.fullName("Student One");
                        studentDsl.repoUrl("url");
                    }
                });
            }
        });

        assertEquals(1, data.getGroups().size());
        assertEquals("G1", data.getGroups().get(0).getName());
        assertEquals(1, data.getGroups().get(0).getStudents().size());
        assertEquals("S1", data.getGroups().get(0).getStudents().get(0).getGithubNick());
        assertEquals("Student One", data.getGroups().get(0).getStudents().get(0).getFullName());
        assertEquals("url", data.getGroups().get(0).getStudents().get(0).getRepoUrl());
    }

    @Test
    public void testAssignmentDsl() {
        ScriptData data = new ScriptData();
        ConfigDslContext context = new ConfigDslContext(data, null);

        context.assignment(new Closure<Void>(null) {
            public void doCall() {
                ConfigDslContext.AssignmentDsl dsl = (ConfigDslContext.AssignmentDsl) getDelegate();
                dsl.studentGithubNick("S1");
                dsl.taskId("T1");
                dsl.projectPath("path");
                dsl.score(5);
                dsl.submittedAt("2023-01-05");
            }
        });

        assertEquals(1, data.getAssignments().size());
        assertEquals("S1", data.getAssignments().get(0).getStudentGithubNick());
        assertEquals("T1", data.getAssignments().get(0).getTaskId());
        assertEquals("path", data.getAssignments().get(0).getProjectPath());
        assertEquals(5, data.getAssignments().get(0).getScore());
        assertEquals(LocalDate.of(2023, 1, 5), data.getAssignments().get(0).getSubmittedAt());
    }

    @Test
    public void testCheckpointDsl() {
        ScriptData data = new ScriptData();
        ConfigDslContext context = new ConfigDslContext(data, null);

        context.checkpoint(new Closure<Void>(null) {
            public void doCall() {
                ConfigDslContext.CheckpointDsl dsl = (ConfigDslContext.CheckpointDsl) getDelegate();
                dsl.name("CP1");
                dsl.date("2023-02-01");
                dsl.requiredScore(50);
            }
        });

        assertEquals(1, data.getCheckpoints().size());
        assertEquals("CP1", data.getCheckpoints().get(0).getName());
        assertEquals(LocalDate.of(2023, 2, 1), data.getCheckpoints().get(0).getDate());
        assertEquals(50, data.getCheckpoints().get(0).getRequiredScore());
    }

    @Test
    public void testGlobalSettingsDsl() {
        ScriptData data = new ScriptData();
        ConfigDslContext context = new ConfigDslContext(data, null);

        context.globalSettings(new Closure<Void>(null) {
            public void doCall() {
                ConfigDslContext.GlobalSettingsDsl dsl = (ConfigDslContext.GlobalSettingsDsl) getDelegate();
                dsl.testTimeout(100L);
                dsl.buildTimeout(200L);
                dsl.gitTimeout(150L);
                dsl.extraPoints(5);
                dsl.excellentThreshold(85);
                dsl.goodThreshold(65);
                dsl.satisfactoryThreshold(45);
                dsl.semesterStart("2023-09-01");
                dsl.semesterEnd("2023-12-31");
                dsl.activityWeight(0.5);
            }
        });

        assertEquals(100L, data.getGlobalSettings().getTestTimeoutSeconds());
        assertEquals(200L, data.getGlobalSettings().getBuildTimeoutSeconds());
        assertEquals(150L, data.getGlobalSettings().getGitTimeoutSeconds());
        assertEquals(5, data.getGlobalSettings().getExtraPoints());
        assertEquals(85, data.getGlobalSettings().getExcellentThreshold());
        assertEquals(65, data.getGlobalSettings().getGoodThreshold());
        assertEquals(45, data.getGlobalSettings().getSatisfactoryThreshold());
        assertEquals(LocalDate.of(2023, 9, 1), data.getGlobalSettings().getSemesterStart());
        assertEquals(LocalDate.of(2023, 12, 31), data.getGlobalSettings().getSemesterEnd());
        assertEquals(0.5, data.getGlobalSettings().getActivityWeight());
    }

    @Test
    public void testImportConfig() {
        ConfigLoader mockLoader = Mockito.mock(ConfigLoader.class);
        ConfigDslContext context = new ConfigDslContext(new ScriptData(), mockLoader);

        context.importConfig("other.groovy");

        verify(mockLoader).importConfig("other.groovy");
    }
}
