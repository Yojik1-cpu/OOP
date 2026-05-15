package service.utils;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class SimpleLoggerTest {

    @Test
    public void testLoggerOutputs() {
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        
        try {
            System.setOut(new PrintStream(outContent));
            System.setErr(new PrintStream(errContent));

            SimpleLogger.info("Test Info");
            SimpleLogger.warn("Test Warn");
            SimpleLogger.error("Test Error");
            SimpleLogger.error("Test Exception", new RuntimeException("test"));

            String out = outContent.toString();
            String err = errContent.toString();

            assertTrue(out.contains("[INFO] Test Info"));
            assertTrue(out.contains("[WARN] Test Warn"));
            assertTrue(err.contains("[ERROR] Test Error"));
            assertTrue(err.contains("java.lang.RuntimeException: test"));
            
            assertFalse(err.contains("[INFO]"));
        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
    }
}
