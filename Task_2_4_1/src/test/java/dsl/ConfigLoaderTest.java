package dsl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigLoaderTest {

    @TempDir
    Path tempDir;

    @Test
    public void testLoad_SimpleConfig() throws IOException {
        Path configFile = tempDir.resolve("config.groovy");
        String content = "task { id 'T1'; title 'Task 1' }";
        Files.writeString(configFile, content);

        ConfigLoader loader = new ConfigLoader();
        ScriptData data = loader.load(configFile);

        assertEquals(1, data.getTasks().size());
        assertEquals("T1", data.getTasks().get(0).getId());
    }

    @Test
    public void testLoad_WithImport() throws IOException {
        Path tasksFile = tempDir.resolve("tasks.groovy");
        String tasksContent = "task { id 'T1'; title 'Task 1' }";
        Files.writeString(tasksFile, tasksContent);

        Path configFile = tempDir.resolve("config.groovy");
        String configContent = "importConfig 'tasks.groovy'\n" +
                               "group { name 'G1' }";
        Files.writeString(configFile, configContent);

        ConfigLoader loader = new ConfigLoader();
        ScriptData data = loader.load(configFile);

        assertEquals(1, data.getTasks().size());
        assertEquals("T1", data.getTasks().get(0).getId());
        assertEquals(1, data.getGroups().size());
    }

    @Test
    public void testLoad_CircularImport() throws IOException {
        Path fileA = tempDir.resolve("a.groovy");
        Files.writeString(fileA, "importConfig 'b.groovy'; task { id 'A' }");

        Path fileB = tempDir.resolve("b.groovy");
        Files.writeString(fileB, "importConfig 'a.groovy'; task { id 'B' }");

        ConfigLoader loader = new ConfigLoader();
        ScriptData data = loader.load(fileA);

        assertEquals(2, data.getTasks().size());
    }

    @Test
    public void testLoad_FileNotFound() {
        ConfigLoader loader = new ConfigLoader();
        Path nonExistentFile = tempDir.resolve("nonexistent.groovy");
        assertThrows(IllegalArgumentException.class, () -> loader.load(nonExistentFile));
    }

    @Test
    public void testLoad_ImportNotFound() throws IOException {
        Path configFile = tempDir.resolve("config.groovy");
        Files.writeString(configFile, "importConfig 'nonexistent.groovy'");

        ConfigLoader loader = new ConfigLoader();
        assertThrows(IllegalArgumentException.class, () -> loader.load(configFile));
    }

    @Test
    public void testDsl_IllegalArgument() throws IOException {
        Path configFile = tempDir.resolve("config.groovy");
        Files.writeString(configFile, "task 'not a closure'");

        ConfigLoader loader = new ConfigLoader();
        assertThrows(IllegalArgumentException.class, () -> loader.load(configFile));
    }
}
