package PizzeriaCore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class BackupManager {
    private static final String BACKUP_FILE = "backup.dat";
    private final OrderLogger logger;

    public BackupManager(OrderLogger logger) {
        this.logger = logger;
    }

    public void serializeOrders(SharedQueue<PizzaOrder> orderQueue, Warehouse warehouse,
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
            logger.log("Serialized " + unfinishedOrders.size() + " unfinished orders.");
        }
    }

    public int deserializeOrders(SharedQueue<PizzaOrder> orderQueue) {
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
                logger.log("Deserialized " + unfinishedOrders.size() + " unfinished orders.");
                if (!backupFile.delete()) {
                    logger.logError("Failed to delete backup file.");
                }
            } catch (ClassNotFoundException | IOException e) {
                logger.logError("Failed to deserialize orders (class changed or file corrupted): " + e.getMessage());
                if (backupFile.delete()) {
                    logger.log("Corrupted backup file deleted.");
                }
            }
        }
        return maxId;
    }
}
