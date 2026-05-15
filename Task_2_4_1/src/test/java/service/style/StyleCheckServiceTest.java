package service.style;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

public class StyleCheckServiceTest {

    @TempDir
    Path tempDir;

    @Test
    public void testCheckStyle_NoConfig() throws IOException {
        Path repoDir = tempDir.resolve("repo");
        Path projectDir = repoDir.resolve("proj");
        Files.createDirectories(projectDir);
        Files.writeString(projectDir.resolve("Main.java"), "public class Main {}");

        StyleCheckService.StyleResult result = StyleCheckService.checkStyle(repoDir, projectDir);
        assertFalse(result.passed);
        assertTrue(result.message.contains("not found"));
    }

    @Test
    public void testCheckStyle_NoJavaFiles() throws IOException {
        Path repoDir = tempDir.resolve("repo");
        Path dotGithub = repoDir.resolve(".github");
        Files.createDirectories(dotGithub);
        Files.writeString(dotGithub.resolve("google_checks.xml"), "<module name='Checker'></module>");
        
        Path projectDir = repoDir.resolve("proj");
        Files.createDirectories(projectDir);

        StyleCheckService.StyleResult result = StyleCheckService.checkStyle(repoDir, projectDir);
        assertTrue(result.passed);
        assertTrue(result.message.contains("No Java files"));
    }
}
