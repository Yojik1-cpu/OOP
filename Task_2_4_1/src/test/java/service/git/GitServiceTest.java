package service.git;

import model.GlobalSettings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import service.utils.ProcessUtils;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;

public class GitServiceTest {

    @TempDir
    Path tempDir;

    @Test
    public void testVerifyGitConfig_Success() {
        try (MockedStatic<ProcessUtils> mockedProcessUtils = Mockito.mockStatic(ProcessUtils.class)) {
            // Mock user.name
            mockedProcessUtils.when(() -> ProcessUtils.runCommand(
                    eq(List.of("git", "config", "--get", "user.name")), isNull(), anyLong()))
                    .thenReturn(new ProcessUtils.CommandResult(0, "Test User"));
            
            // Mock credential.helper
            mockedProcessUtils.when(() -> ProcessUtils.runCommand(
                    eq(List.of("git", "config", "--get", "credential.helper")), isNull(), anyLong()))
                    .thenReturn(new ProcessUtils.CommandResult(0, "manager"));
            
            // Mock core.askPass (just in case)
            mockedProcessUtils.when(() -> ProcessUtils.runCommand(
                    eq(List.of("git", "config", "--get", "core.askPass")), isNull(), anyLong()))
                    .thenReturn(new ProcessUtils.CommandResult(1, ""));

            GitService gitService = new GitService();
            assertDoesNotThrow(() -> gitService.verifyGitConfig());
        }
    }

    @Test
    public void testVerifyGitConfig_FailureNoUser() {
        try (MockedStatic<ProcessUtils> mockedProcessUtils = Mockito.mockStatic(ProcessUtils.class)) {
            mockedProcessUtils.when(() -> ProcessUtils.runCommand(
                    eq(List.of("git", "config", "--get", "user.name")), isNull(), anyLong()))
                    .thenReturn(new ProcessUtils.CommandResult(1, ""));

            GitService gitService = new GitService();
            assertThrows(IllegalStateException.class, () -> gitService.verifyGitConfig());
        }
    }

    @Test
    public void testCloneRepository_Success() throws Exception {
        GlobalSettings settings = new GlobalSettings();
        
        try (MockedStatic<ProcessUtils> mockedProcessUtils = Mockito.mockStatic(ProcessUtils.class)) {
            mockedProcessUtils.when(() -> ProcessUtils.runCommand(
                    eq(List.of("git", "clone", "url", tempDir.toString())), isNull(), anyLong()))
                    .thenReturn(new ProcessUtils.CommandResult(0, ""));

            mockedProcessUtils.when(() -> ProcessUtils.runCommand(
                    eq(List.of("git", "branch", "-r")), eq(tempDir), anyLong()))
                    .thenReturn(new ProcessUtils.CommandResult(0, "  origin/main\n"));

            mockedProcessUtils.when(() -> ProcessUtils.runCommand(
                    eq(List.of("git", "checkout", "main")), eq(tempDir), anyLong()))
                    .thenReturn(new ProcessUtils.CommandResult(0, ""));

            GitService gitService = new GitService();
            assertTrue(gitService.cloneRepository("url", tempDir, settings));
        }
    }

    @Test
    public void testCloneRepository_CloneFails() throws Exception {
        GlobalSettings settings = new GlobalSettings();
        
        try (MockedStatic<ProcessUtils> mockedProcessUtils = Mockito.mockStatic(ProcessUtils.class)) {
            mockedProcessUtils.when(() -> ProcessUtils.runCommand(
                    eq(List.of("git", "clone", "url", tempDir.toString())), isNull(), anyLong()))
                    .thenReturn(new ProcessUtils.CommandResult(1, "Error"));

            GitService gitService = new GitService();
            assertFalse(gitService.cloneRepository("url", tempDir, settings));
        }
    }

    @Test
    public void testGetCommitDates_Success() {
        try (MockedStatic<ProcessUtils> mockedProcessUtils = Mockito.mockStatic(ProcessUtils.class)) {
            mockedProcessUtils.when(() -> ProcessUtils.runCommand(
                    eq(List.of("git", "log", "--all", "--pretty=format:%as")), eq(tempDir), anyLong()))
                    .thenReturn(new ProcessUtils.CommandResult(0, "2023-10-01\n2023-10-05\ninvalid_date"));

            GitService gitService = new GitService();
            List<LocalDate> dates = gitService.getCommitDates(tempDir);
            assertEquals(2, dates.size());
            assertEquals(LocalDate.of(2023, 10, 1), dates.get(0));
            assertEquals(LocalDate.of(2023, 10, 5), dates.get(1));
        }
    }

    @Test
    public void testGetCommitDates_Empty() {
        try (MockedStatic<ProcessUtils> mockedProcessUtils = Mockito.mockStatic(ProcessUtils.class)) {
            mockedProcessUtils.when(() -> ProcessUtils.runCommand(
                    eq(List.of("git", "log", "--all", "--pretty=format:%as")), eq(tempDir), anyLong()))
                    .thenReturn(new ProcessUtils.CommandResult(0, ""));

            GitService gitService = new GitService();
            List<LocalDate> dates = gitService.getCommitDates(tempDir);
            assertTrue(dates.isEmpty());
        }
    }

    @Test
    public void testGetFirstCommitDate_Success() {
        try (MockedStatic<ProcessUtils> mockedProcessUtils = Mockito.mockStatic(ProcessUtils.class)) {
            mockedProcessUtils.when(() -> ProcessUtils.runCommand(
                    eq(List.of("git", "log", "--all", "--reverse", "--format=%as", "--", "path")), eq(tempDir), anyLong()))
                    .thenReturn(new ProcessUtils.CommandResult(0, "2023-10-01\n2023-10-02"));

            GitService gitService = new GitService();
            LocalDate date = gitService.getFirstCommitDate(tempDir, "path");
            assertEquals(LocalDate.of(2023, 10, 1), date);
        }
    }

    @Test
    public void testGetLastCommitDate_Success() {
        try (MockedStatic<ProcessUtils> mockedProcessUtils = Mockito.mockStatic(ProcessUtils.class)) {
            mockedProcessUtils.when(() -> ProcessUtils.runCommand(
                    eq(List.of("git", "log", "--all", "-1", "--format=%as", "--", "path")), eq(tempDir), anyLong()))
                    .thenReturn(new ProcessUtils.CommandResult(0, "2023-10-15"));

            GitService gitService = new GitService();
            LocalDate date = gitService.getLastCommitDate(tempDir, "path");
            assertEquals(LocalDate.of(2023, 10, 15), date);
        }
    }
}
