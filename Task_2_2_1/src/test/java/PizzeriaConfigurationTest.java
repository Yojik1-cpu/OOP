import PizzeriaConfig.BakerConfig;
import PizzeriaConfig.CourierConfig;
import PizzeriaConfig.PizzeriaConfiguration;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PizzeriaConfigurationTest {

    @Test
    void testConfigurationGetters() {
        List<BakerConfig> bakers = Collections.singletonList(new BakerConfig(100));
        List<CourierConfig> couriers = Collections.singletonList(new CourierConfig(2));
        int warehouseCapacity = 10;
        long newOrdersDeadline = 5000;
        long shutdownDeadline = 10000;

        PizzeriaConfiguration config = new PizzeriaConfiguration(
            bakers, couriers, warehouseCapacity, newOrdersDeadline, shutdownDeadline
        );

        assertEquals(bakers, config.getBakers());
        assertEquals(couriers, config.getCouriers());
        assertEquals(warehouseCapacity, config.getWarehouseCapacity());
        assertEquals(newOrdersDeadline, config.getNewOrdersDeadline());
        assertEquals(shutdownDeadline, config.getShutdownDeadline());
    }
}
