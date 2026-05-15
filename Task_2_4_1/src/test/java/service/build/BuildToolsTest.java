package service.build;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

public class BuildToolsTest {

    @TempDir
    Path tempDir;

    @Test
    public void testGradleBuildToolCanHandle() throws IOException {
        GradleBuildTool tool = new GradleBuildTool();
        assertFalse(tool.canHandle(tempDir));
        Files.writeString(tempDir.resolve("build.gradle"), "");
        assertTrue(tool.canHandle(tempDir));
    }

    @Test
    public void testMavenBuildToolCanHandle() throws IOException {
        MavenBuildTool tool = new MavenBuildTool();
        assertFalse(tool.canHandle(tempDir));
        Files.writeString(tempDir.resolve("pom.xml"), "");
        assertTrue(tool.canHandle(tempDir));
    }

    @Test
    public void testJavacBuildToolCanHandle() throws IOException {
        JavacBuildTool tool = new JavacBuildTool();
        assertFalse(tool.canHandle(tempDir));
        Path src = tempDir.resolve("src");
        Files.createDirectories(src);
        Files.writeString(src.resolve("A.java"), "class A{}");
        assertTrue(tool.canHandle(tempDir));
    }
}
