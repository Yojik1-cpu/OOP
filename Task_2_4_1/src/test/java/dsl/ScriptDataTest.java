package dsl;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ScriptDataTest {

    @Test
    public void testScriptDataInitialization() {
        ScriptData data = new ScriptData();
        assertNotNull(data.getTasks());
        assertNotNull(data.getGroups());
        assertNotNull(data.getAssignments());
        assertNotNull(data.getCheckpoints());
        assertNotNull(data.getGlobalSettings());
        
        assertTrue(data.getTasks().isEmpty());
        assertTrue(data.getGroups().isEmpty());
    }
}
