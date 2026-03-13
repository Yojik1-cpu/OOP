import Pizza.Pizzeria;
import PizzeriaCore.PizzaOrder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PizzeriaTest {

    private final Path configPath = Paths.get("src/main/resources/pizzeria-config.json");
    private final Path configBackupPath = Paths.get("src/main/resources/pizzeria-config.json.bak");
    private final File backupFile = new File("backup.dat");

    @BeforeEach
    void setUp() throws IOException {
        if (Files.exists(configPath)) {
            Files.copy(configPath, configBackupPath, StandardCopyOption.REPLACE_EXISTING);
        }
        if (backupFile.exists()) {
            backupFile.delete();
        }
    }

    @Test
    void testPizzeriaLifecycleAndSerialization() throws Exception {
        String testConfig = "{\n" +
                "  \"bakers\": [ { \"cookingTime\": 100 }, { \"cookingTime\": 100 } ],\n" +
                "  \"couriers\": [ { \"trunkCapacity\": 1 } ],\n" +
                "  \"warehouseCapacity\": 2,\n" +
                "  \"newOrdersDeadline\": 1000,\n" +
                "  \"shutdownDeadline\": 1500\n" +
                "}";
        Files.write(configPath, testConfig.getBytes());

        Pizzeria.main(null);

        assertTrue(backupFile.exists(), "backup.dat should have been created");

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(backupFile))) {
            Object obj = ois.readObject();
            assertTrue(obj instanceof List, "Backup file should contain a List");
            List<?> list = (List<?>) obj;
            assertFalse(list.isEmpty(), "List of unfinished orders should not be empty");
            assertTrue(list.get(0) instanceof PizzaOrder, "List should contain PizzaOrder objects");
            
            System.out.println("Found " + list.size() + " unfinished orders in backup.dat, which is expected.");
        }
    }

    @AfterEach
    void tearDown() throws IOException {
        if (Files.exists(configBackupPath)) {
            Files.move(configBackupPath, configPath, StandardCopyOption.REPLACE_EXISTING);
        }
        if (backupFile.exists()) {
            backupFile.delete();
        }
    }
}
