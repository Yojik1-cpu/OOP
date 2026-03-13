import PizzeriaConfig.CourierConfig;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CourierConfigTest {

    @Test
    void testGetTrunkCapacity() {
        int expectedCapacity = 5;
        CourierConfig config = new CourierConfig(expectedCapacity);
        assertEquals(expectedCapacity, config.getTrunkCapacity());
    }
}
