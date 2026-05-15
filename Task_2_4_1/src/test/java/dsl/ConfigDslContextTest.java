package dsl;

import groovy.lang.Closure;
import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

public class ConfigDslContextTest {

    @Test
    public void testTaskDsl() {
        ScriptData data = new ScriptData();
        ConfigLoader loader = new ConfigLoader();
        ConfigDslContext context = new ConfigDslContext(data, loader, Path.of("."));
        
        context.task(new Closure<Void>(null) {
            public void doCall() {
                ((ConfigDslContext.TaskDsl)getDelegate()).id("T1");
                ((ConfigDslContext.TaskDsl)getDelegate()).title("Test");
            }
        });

        assertEquals(1, data.getTasks().size());
        assertEquals("T1", data.getTasks().get(0).getId());
        assertEquals("Test", data.getTasks().get(0).getTitle());
    }

    @Test
    public void testGlobalSettingsDsl() {
        ScriptData data = new ScriptData();
        ConfigLoader loader = new ConfigLoader();
        ConfigDslContext context = new ConfigDslContext(data, loader, Path.of("."));

        context.globalSettings(new Closure<Void>(null) {
            public void doCall() {
                ((ConfigDslContext.GlobalSettingsDsl)getDelegate()).extraPoints(10);
            }
        });

        assertEquals(10, data.getGlobalSettings().getExtraPoints());
    }
}
