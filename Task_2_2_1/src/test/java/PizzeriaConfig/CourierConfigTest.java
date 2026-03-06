package PizzeriaConfig;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CourierConfigTest {
    @Test
    void testGetTrunkCapacity() {
        CourierConfig config = new CourierConfig(5);
        assertEquals(5, config.getTrunkCapacity());
    }
}
