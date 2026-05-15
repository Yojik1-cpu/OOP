package service.utils;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

            assertTrue(outContent.toString().contains("[INFO] Test Info"));
            assertTrue(outContent.toString().contains("[WARN] Test Warn"));
            assertTrue(errContent.toString().contains("[ERROR] Test Error"));
        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
    }
}
