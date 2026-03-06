package PizzeriaConfig;

import org.junit.jupiter.api.Test;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;

public class PizzeriaConfigurationTest {
    @Test
    void testGetters() {
        PizzeriaConfiguration config = new PizzeriaConfiguration(
                Collections.singletonList(new BakerConfig(100)),
                Collections.singletonList(new CourierConfig(2)),
                10,
                5000,
                10000
        );

        assertEquals(1, config.getBakers().size());
        assertEquals(1, config.getCouriers().size());
        assertEquals(10, config.getWarehouseCapacity());
        assertEquals(5000, config.getNewOrdersDeadline());
        assertEquals(10000, config.getShutdownDeadline());
    }
}
