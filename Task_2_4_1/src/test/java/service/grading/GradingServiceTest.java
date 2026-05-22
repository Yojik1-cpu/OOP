package service.grading;

import dsl.ScriptData;
import model.*;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import service.build.GradleBuildTool;
import service.build.TestStats;
import service.git.GitService;
import service.utils.ProcessUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

public class GradingServiceTest {

    @Test
    public void testExecuteGrading_Success() {
        try (MockedConstruction<GitService> mockedGit = Mockito.mockConstruction(GitService.class, (mock, context) -> {
            Mockito.when(mock.cloneRepository(any(), any(), any())).thenAnswer(invocation -> {
                Path repoDir = invocation.getArgument(1);
                Files.createDirectories(repoDir.resolve("Task1"));
                return true;
            });
            Mockito.when(mock.getCommitDates(any())).thenReturn(List.of(LocalDate.of(2023, 10, 1)));
            Mockito.when(mock.getFirstCommitDate(any(), any())).thenReturn(LocalDate.of(2023, 10, 1));
            Mockito.when(mock.getLastCommitDate(any(), any())).thenReturn(LocalDate.of(2023, 10, 2));
        });
             MockedConstruction<GradleBuildTool> mockedGradle = Mockito.mockConstruction(GradleBuildTool.class, (mock, context) -> {
                 Mockito.when(mock.canHandle(any())).thenReturn(true);
                 Mockito.when(mock.compile(any(), any())).thenReturn(new ProcessUtils.CommandResult(0, ""));
                 Mockito.when(mock.generateDocs(any(), any())).thenReturn(new ProcessUtils.CommandResult(0, ""));
                 Mockito.when(mock.runTests(any(), any())).thenReturn(new TestStats(10, 0, 0, true, "", 1.0));
             })) {

            GradingService gradingService = new GradingService();

            ScriptData data = new ScriptData();
            GlobalSettings settings = data.getGlobalSettings();
            settings.setSemesterStart(LocalDate.of(2023, 9, 1));
            settings.setSemesterEnd(LocalDate.of(2023, 12, 31));

            LabTask task = new LabTask();
            task.setId("Task1");
            task.setMaxScore(10);
            data.getTasks().add(task);

            Student student = new Student();
            student.setGithubNick("nick");
            student.setRepoUrl("url");
            StudentGroup group = new StudentGroup();
            group.addStudent(student);
            data.getGroups().add(group);

            Assignment assignment = new Assignment();
            assignment.setStudentGithubNick("nick");
            assignment.setTaskId("Task1");
            data.getAssignments().add(assignment);

            Checkpoint checkpoint = new Checkpoint();
            checkpoint.setName("CP1");
            checkpoint.setDate(LocalDate.of(2023, 10, 15));
            data.getCheckpoints().add(checkpoint);

            FinalReport report = gradingService.executeGrading(data);

            assertNotNull(report);
            assertEquals(1, report.getTotalAssignments());
            assertEquals(1, report.getSuccessfulAssignments());
            assertEquals(0, report.getFailedAssignments());

            assertEquals(1, report.getRows().size());
            FinalReport.GradingRow row = report.getRows().get(0);
            assertEquals("nick", row.getStudentGithubNick());
            assertEquals("Task1", row.getTaskId());
            assertTrue(row.isGitOk());
            assertTrue(row.isBuildOk());
            assertTrue(row.isDocsOk());

            assertEquals(1, report.getStudentSummaries().size());
            FinalReport.StudentSummary summary = report.getStudentSummaries().get(0);
            assertEquals("nick", summary.getGithubNick());
            assertTrue(summary.getTotalScore() > 0);

            gradingService.close();
        }
    }

    @Test
    public void testExecuteGrading_GitCloneFails() {
        try (MockedConstruction<GitService> mockedGit = Mockito.mockConstruction(GitService.class, (mock, context) -> {
            Mockito.when(mock.cloneRepository(any(), any(), any())).thenReturn(false);
        })) {

            GradingService gradingService = new GradingService();

            ScriptData data = new ScriptData();
            LabTask task = new LabTask();
            task.setId("Task1");
            data.getTasks().add(task);

            Student student = new Student();
            student.setGithubNick("nick");
            student.setRepoUrl("url");
            StudentGroup group = new StudentGroup();
            group.addStudent(student);
            data.getGroups().add(group);

            Assignment assignment = new Assignment();
            assignment.setStudentGithubNick("nick");
            assignment.setTaskId("Task1");
            data.getAssignments().add(assignment);

            FinalReport report = gradingService.executeGrading(data);

            assertEquals(1, report.getTotalAssignments());
            assertEquals(0, report.getSuccessfulAssignments());
            assertEquals(1, report.getFailedAssignments());

            FinalReport.GradingRow row = report.getRows().get(0);
            assertFalse(row.isGitOk());
            assertEquals("Git clone failed", row.getMessage());

            gradingService.close();
        }
    }

    @Test
    public void testExecuteGrading_StudentNotFound() {
        try (MockedConstruction<GitService> mockedGit = Mockito.mockConstruction(GitService.class)) {
            GradingService gradingService = new GradingService();

            ScriptData data = new ScriptData();
            Assignment assignment = new Assignment();
            assignment.setStudentGithubNick("unknown");
            assignment.setTaskId("Task1");
            data.getAssignments().add(assignment);

            FinalReport report = gradingService.executeGrading(data);

            assertEquals(1, report.getTotalAssignments());
            assertEquals(0, report.getSuccessfulAssignments());
            assertEquals(1, report.getFailedAssignments());

            FinalReport.GradingRow row = report.getRows().get(0);
            assertEquals("Student not found", row.getMessage());

            gradingService.close();
        }
    }
}
