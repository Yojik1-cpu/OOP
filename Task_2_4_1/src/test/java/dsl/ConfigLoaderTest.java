package dsl;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigLoaderTest {

    private static class TestConfigLoader extends ConfigLoader {
        private final Map<String, String> resources = new HashMap<>();

        public void addResource(String path, String content) {
            resources.put(path, content);
        }

        @Override
        protected String readResource(String resourcePath) {
            return resources.get(resourcePath);
        }
    }

    @Test
    public void testLoad_SimpleConfig() {
        TestConfigLoader loader = new TestConfigLoader();
        loader.addResource("config.groovy", "task { id 'T1'; title 'Task 1' }");
        
        ScriptData data = loader.load("config.groovy");

        assertEquals(1, data.getTasks().size());
        assertEquals("T1", data.getTasks().get(0).getId());
    }

    @Test
    public void testLoad_WithImport() {
        TestConfigLoader loader = new TestConfigLoader();
        loader.addResource("tasks.groovy", "task { id 'T1'; title 'Task 1' }");
        loader.addResource("config.groovy", "importConfig 'tasks.groovy'; group { name 'G1' }");

        ScriptData data = loader.load("config.groovy");

        assertEquals(1, data.getTasks().size());
        assertEquals("T1", data.getTasks().get(0).getId());
        assertEquals(1, data.getGroups().size());
    }

    @Test
    public void testLoad_CircularImport() {
        TestConfigLoader loader = new TestConfigLoader();
        loader.addResource("a.groovy", "importConfig 'b.groovy'; task { id 'A' }");
        loader.addResource("b.groovy", "importConfig 'a.groovy'; task { id 'B' }");

        ScriptData data = loader.load("a.groovy");

        assertEquals(2, data.getTasks().size());
    }

    @Test
    public void testLoad_FileNotFound() {
        TestConfigLoader loader = new TestConfigLoader();
        assertThrows(IllegalArgumentException.class, () -> loader.load("nonexistent.groovy"));
    }

    @Test
    public void testLoad_ImportNotFound() {
        TestConfigLoader loader = new TestConfigLoader();
        loader.addResource("config.groovy", "importConfig 'nonexistent.groovy'");
        assertThrows(IllegalArgumentException.class, () -> loader.load("config.groovy"));
    }

    @Test
    public void testDsl_IllegalArgument() {
        TestConfigLoader loader = new TestConfigLoader();
        loader.addResource("config.groovy", "task 'not a closure'");
        assertThrows(IllegalArgumentException.class, () -> loader.load("config.groovy"));
    }
}
