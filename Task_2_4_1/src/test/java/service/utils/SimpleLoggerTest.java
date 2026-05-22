package service.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SimpleLoggerTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    public void testInfo() {
        SimpleLogger.info("Test Info");
        assertTrue(outContent.toString().contains("[INFO] Test Info"));
    }

    @Test
    public void testWarn() {
        SimpleLogger.warn("Test Warn");
        assertTrue(outContent.toString().contains("[WARN] Test Warn"));
    }

    @Test
    public void testError() {
        SimpleLogger.error("Test Error");
        assertTrue(errContent.toString().contains("[ERROR] Test Error"));
    }

    @Test
    public void testErrorWithException() {
        SimpleLogger.error("Test Exception", new RuntimeException("test"));
        String err = errContent.toString();
        assertTrue(err.contains("[ERROR] Test Exception"));
        assertTrue(err.contains("java.lang.RuntimeException: test"));
    }
}
