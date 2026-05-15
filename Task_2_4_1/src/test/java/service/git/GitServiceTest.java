package service.git;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class GitServiceTest {

    @TempDir
    Path tempDir;

    @Test
    public void testGetCommitDates_EmptyDir() {
        GitService gitService = new GitService();
        List<LocalDate> dates = gitService.getCommitDates(tempDir);
        assertTrue(dates.isEmpty());
    }

    @Test
    public void testGetFirstAndLastCommitDates_NonGitDir() {
        GitService gitService = new GitService();
        assertNull(gitService.getFirstCommitDate(tempDir, "."));
        assertNull(gitService.getLastCommitDate(tempDir, "."));
    }
}
