package service.utils;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ProcessUtilsTest {

    @Test
    public void testRunCommand_Success() throws IOException, InterruptedException {
        String os = System.getProperty("os.name").toLowerCase();
        List<String> command = os.contains("win") ? List.of("cmd", "/c", "echo", "hello") : List.of("echo", "hello");
        
        ProcessUtils.CommandResult result = ProcessUtils.runCommand(command, Path.of("."), 10);
        
        assertEquals(0, result.exitCode);
        assertTrue(result.output.contains("hello"));
    }

    @Test
    public void testRunCommand_InvalidCommand() {
        assertThrows(IOException.class, () -> {
            ProcessUtils.runCommand(List.of("non-existent-command-12345"), Path.of("."), 5);
        });
    }
}
