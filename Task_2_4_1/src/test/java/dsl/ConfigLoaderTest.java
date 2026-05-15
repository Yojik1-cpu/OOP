package dsl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConfigLoaderTest {

    @TempDir
    Path tempDir;

    @Test
    public void testLoad_SimpleConfig() throws IOException {
        Path configFile = tempDir.resolve("config.groovy");
        String content = "task { id 'T1'; title 'Task 1'; maxScore 5 }\n" +
                         "group { name 'G1'; student { githubNick 'S1' } }\n" +
                         "assignment { studentGithubNick 'S1'; taskId 'T1'; score 4 }";
        Files.writeString(configFile, content);

        ConfigLoader loader = new ConfigLoader();
        ScriptData data = loader.load(configFile);

        assertEquals(1, data.getTasks().size());
        assertEquals("T1", data.getTasks().get(0).getId());
        assertEquals(1, data.getGroups().size());
        assertEquals(1, data.getAssignments().size());
        assertEquals(4, data.getAssignments().get(0).getScore());
    }
}
