import PizzeriaCore.OrderLogger;
import PizzeriaCore.PizzaOrder;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderLoggerTest {

    @Test
    void testLogOrder() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        OrderLogger logger = new OrderLogger();
        PizzaOrder order = new PizzaOrder(1);
        logger.log(order);

        String output = outContent.toString().trim();
        assertTrue(output.contains("[1] QUEUED"));

        System.setOut(System.out);
    }

    @Test
    void testLogMessage() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        OrderLogger logger = new OrderLogger();
        logger.log("Test message");

        String output = outContent.toString().trim();
        assertEquals("Test message", output);

        System.setOut(System.out);
    }

    @Test
    void testLogError() {
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        OrderLogger logger = new OrderLogger();
        logger.logError("Error message");

        String output = errContent.toString().trim();
        assertEquals("Error message", output);

        System.setErr(System.err);
    }
}
