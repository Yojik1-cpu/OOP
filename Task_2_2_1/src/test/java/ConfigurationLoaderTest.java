import PizzeriaConfig.ConfigurationLoader;
import PizzeriaConfig.PizzeriaConfiguration;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConfigurationLoaderTest {

    @Test
    void testLoadConfiguration() throws IOException {
        PizzeriaConfiguration config = ConfigurationLoader.load("/pizzeria-config.json");
        assertNotNull(config);
        assertNotNull(config.getBakers());
        assertNotNull(config.getCouriers());
    }

    @Test
    void testLoadMissingConfiguration() {
        assertThrows(IOException.class, () -> ConfigurationLoader.load("/non-existent-config.json"));
    }
}
