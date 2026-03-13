import PizzeriaConfig.BakerConfig;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BakerConfigTest {

    @Test
    void testGetCookingTime() {
        int expectedCookingTime = 500;
        BakerConfig config = new BakerConfig(expectedCookingTime);
        assertEquals(expectedCookingTime, config.getCookingTime());
    }
}
