package PizzeriaConfig;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ConfigurationLoader {

    public static PizzeriaConfiguration load(String configFile) throws IOException {
        List<BakerConfig> bakers = new ArrayList<>();
        List<CourierConfig> couriers = new ArrayList<>();
        int warehouseCapacity = 0;
        long newOrdersDeadline = 0;
        long shutdownDeadline = 0;

        try (InputStream is = ConfigurationLoader.class.getResourceAsStream(configFile)) {
            if (is == null) {
                throw new FileNotFoundException("Cannot find configuration file: " + configFile);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                boolean inBakers = false;
                boolean inCouriers = false;

                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.startsWith("\"bakers\":")) {
                        inBakers = true;
                        inCouriers = false;
                    } else if (line.startsWith("\"couriers\":")) {
                        inBakers = false;
                        inCouriers = true;
                    } else if (line.startsWith("\"warehouseCapacity\":")) {
                        warehouseCapacity = Integer.parseInt(line.replaceAll("[^0-9]", ""));
                    } else if (line.startsWith("\"newOrdersDeadline\":")) {
                        newOrdersDeadline = Long.parseLong(line.replaceAll("[^0-9]", ""));
                    } else if (line.startsWith("\"shutdownDeadline\":")) {
                        shutdownDeadline = Long.parseLong(line.replaceAll("[^0-9]", ""));
                    } else if (line.contains("cookingTime") && inBakers) {
                        int time = Integer.parseInt(line.replaceAll("[^0-9]", ""));
                        bakers.add(new BakerConfig(time));
                    } else if (line.contains("trunkCapacity") && inCouriers) {
                        int capacity = Integer.parseInt(line.replaceAll("[^0-9]", ""));
                        couriers.add(new CourierConfig(capacity));
                    }
                }
            }
        }
        return new PizzeriaConfiguration(bakers, couriers, warehouseCapacity, newOrdersDeadline, shutdownDeadline);
    }
}