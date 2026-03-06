package PizzeriaConfig;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BakerConfigTest {
    @Test
    void testGetCookingTime() {
        BakerConfig config = new BakerConfig(1000);
        assertEquals(1000, config.getCookingTime());
    }
}
