import PizzeriaConfig.BakerConfig;
import PizzeriaConfig.CourierConfig;
import PizzeriaConfig.PizzeriaConfiguration;
import PizzeriaCore.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Pizzeria {

    private static final String CONFIG_FILE = "/pizzeria-config.json";
    private static final String BACKUP_FILE = "backup.dat";

    public static void main(String[] args) throws IOException, InterruptedException {
        PizzeriaConfiguration config = loadConfiguration();

        SharedQueue<PizzaOrder> orderQueue = new SharedQueue<>(Integer.MAX_VALUE);
        Warehouse warehouse = new Warehouse(config.getWarehouseCapacity());

        List<Baker> bakers = new ArrayList<>();
        for (int i = 0; i < config.getBakers().size(); i++) {
            bakers.add(new Baker(i, config.getBakers().get(i).getCookingTime(), orderQueue, warehouse));
        }

        List<Courier> couriers = new ArrayList<>();
        for (int i = 0; i < config.getCouriers().size(); i++) {
            couriers.add(new Courier(i, config.getCouriers().get(i).getTrunkCapacity(), warehouse));
        }

        ExecutorService executor = Executors.newCachedThreadPool();
        bakers.forEach(executor::submit);
        couriers.forEach(executor::submit);

        int maxId = deserializeOrders(orderQueue);

        long startTime = System.currentTimeMillis();
        int orderIdCounter = maxId + 1;

        while (System.currentTimeMillis() - startTime < config.getNewOrdersDeadline()) {
            PizzaOrder order = new PizzaOrder(orderIdCounter++);
            orderQueue.add(order);
            order.setState(PizzaOrder.State.QUEUED);
            System.out.println("Order " + order.getId() + " is queued");
            Thread.sleep(500); 
        }

        System.out.println("No more new orders are accepted.");
        orderQueue.stop();

        while (System.currentTimeMillis() - startTime < config.getShutdownDeadline()) {
            if (orderQueue.size() == 0 && warehouse.size() == 0) {
                break;
            }
            Thread.sleep(1000);
        }

        System.out.println("Pizzeria is shutting down.");
        bakers.forEach(Baker::stop);
        couriers.forEach(Courier::stop);
        warehouse.stop();

        executor.shutdownNow();
        if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
            System.err.println("Executor did not terminate in the specified time.");
        }

        serializeOrders(orderQueue, warehouse, bakers, couriers);

        System.out.println("Pizzeria has been shut down.");
    }

    private static PizzeriaConfiguration loadConfiguration() throws IOException {
        List<BakerConfig> bakers = new ArrayList<>();
        List<CourierConfig> couriers = new ArrayList<>();
        int warehouseCapacity = 0;
        long newOrdersDeadline = 0;
        long shutdownDeadline = 0;

        try (InputStream is = Pizzeria.class.getResourceAsStream(CONFIG_FILE)) {
            if (is == null) {
                throw new FileNotFoundException("Cannot find configuration file: " + CONFIG_FILE);
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
                    } else if (line.contains("cookingTime")) {
                        if (inBakers) {
                            int time = Integer.parseInt(line.replaceAll("[^0-9]", ""));
                            bakers.add(new BakerConfig(time));
                        }
                    } else if (line.contains("trunkCapacity")) {
                        if (inCouriers) {
                            int capacity = Integer.parseInt(line.replaceAll("[^0-9]", ""));
                            couriers.add(new CourierConfig(capacity));
                        }
                    }
                }
            }
        }
        return new PizzeriaConfiguration(bakers, couriers, warehouseCapacity, newOrdersDeadline, shutdownDeadline);
    }

    private static void serializeOrders(SharedQueue<PizzaOrder> orderQueue, Warehouse warehouse,
                                        List<Baker> bakers, List<Courier> couriers) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BACKUP_FILE))) {
            List<PizzaOrder> unfinishedOrders = new ArrayList<>();
            orderQueue.drainTo(unfinishedOrders);
            warehouse.drainTo(unfinishedOrders);

            for (Baker baker : bakers) {
                PizzaOrder order = baker.getCurrentOrder();
                if (order != null) {
                    order.setState(PizzaOrder.State.QUEUED);
                    unfinishedOrders.add(order);
                }
            }

            for (Courier courier : couriers) {
                List<PizzaOrder> orders = courier.getCurrentOrders();
                if (orders != null) {
                    for (PizzaOrder order : orders) {
                        order.setState(PizzaOrder.State.QUEUED);
                        unfinishedOrders.add(order);
                    }
                }
            }

            oos.writeObject(unfinishedOrders);
            System.out.println("Serialized " + unfinishedOrders.size() + " unfinished orders.");
        }
    }

    private static int deserializeOrders(SharedQueue<PizzaOrder> orderQueue) {
        File backupFile = new File(BACKUP_FILE);
        int maxId = -1;
        if (backupFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(backupFile))) {
                List<PizzaOrder> unfinishedOrders = (List<PizzaOrder>) ois.readObject();
                for (PizzaOrder order : unfinishedOrders) {
                    try {
                        orderQueue.add(order);
                        if (order.getId() > maxId) {
                            maxId = order.getId();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                System.out.println("Deserialized " + unfinishedOrders.size() + " unfinished orders.");
                if (!backupFile.delete()) {
                    System.err.println("Failed to delete backup file.");
                }
            } catch (ClassNotFoundException | IOException e) {
                System.err.println("Failed to deserialize orders (class changed or file corrupted): " + e.getMessage());
                if (backupFile.delete()) {
                    System.out.println("Corrupted backup file deleted.");
                }
            }
        }
        return maxId;
    }
}
