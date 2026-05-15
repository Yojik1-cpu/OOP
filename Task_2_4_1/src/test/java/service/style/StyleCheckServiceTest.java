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

    @Test
    public void testStyleResultProperties() {
        StyleCheckService.StyleResult result = new StyleCheckService.StyleResult(true, 5, "Msg");
        assertTrue(result.passed);
        assertEquals(5, result.errorsCount);
        assertEquals("Msg", result.message);
    }

    @Test
    public void testCheckStyle_WithViolations() throws IOException {
        Path repoDir = tempDir.resolve("repo");
        Path projectDir = repoDir.resolve("proj");
        Files.createDirectories(projectDir);
        
        // Создаем простой конфиг, который будет ругаться на длинные строки
        String checkstyleConfig = "<?xml version=\"1.0\"?>\n" +
                "<!DOCTYPE module PUBLIC \"-//Puppy Crawl//DTD Check Configuration 1.3//EN\" \"https://checkstyle.org/dtds/configuration_1_3.dtd\">\n" +
                "<module name=\"Checker\">\n" +
                "    <module name=\"LineLength\">\n" +
                "        <property name=\"max\" value=\"20\"/>\n" +
                "    </module>\n" +
                "</module>";
        Files.writeString(repoDir.resolve("google_checks.xml"), checkstyleConfig);

        // Создаем файл с нарушением
        Files.writeString(projectDir.resolve("LongLine.java"), "public class LongLine { String s = \"This is a very very very long line\"; }");

        StyleCheckService.StyleResult result = StyleCheckService.checkStyle(repoDir, projectDir);
        assertFalse(result.passed);
        assertTrue(result.errorsCount > 0);
        assertTrue(result.message.contains("style violations found"));
    }
}
