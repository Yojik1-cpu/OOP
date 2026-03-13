package Pizza;

import PizzeriaConfig.ConfigurationLoader;
import PizzeriaConfig.PizzeriaConfiguration;
import PizzeriaCore.BackupManager;
import PizzeriaCore.Baker;
import PizzeriaCore.Courier;
import PizzeriaCore.OrderLogger;
import PizzeriaCore.PizzaOrder;
import PizzeriaCore.SharedQueue;
import PizzeriaCore.Warehouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Pizzeria {

    private static final String CONFIG_FILE = "/pizzeria-config.json";
    private static final OrderLogger logger = new OrderLogger();

    public static void main(String[] args) throws IOException, InterruptedException {
        PizzeriaConfiguration config = ConfigurationLoader.load(CONFIG_FILE);

        SharedQueue<PizzaOrder> orderQueue = new SharedQueue<>(Integer.MAX_VALUE);
        Warehouse warehouse = new Warehouse(config.getWarehouseCapacity(), logger);

        List<Baker> bakers = new ArrayList<>();
        for (int i = 0; i < config.getBakers().size(); i++) {
            bakers.add(new Baker(i, config.getBakers().get(i).getCookingTime(), orderQueue, warehouse, logger));
        }

        List<Courier> couriers = new ArrayList<>();
        for (int i = 0; i < config.getCouriers().size(); i++) {
            couriers.add(new Courier(i, config.getCouriers().get(i).getTrunkCapacity(), warehouse, logger));
        }

        ExecutorService executor = Executors.newCachedThreadPool();
        bakers.forEach(executor::submit);
        couriers.forEach(executor::submit);

        BackupManager backupManager = new BackupManager(logger);
        int maxId = backupManager.deserializeOrders(orderQueue);

        long startTime = System.currentTimeMillis();
        int orderIdCounter = maxId + 1;

        while (System.currentTimeMillis() - startTime < config.getNewOrdersDeadline()) {
            PizzaOrder order = new PizzaOrder(orderIdCounter++);
            orderQueue.add(order);
            order.setState(PizzaOrder.State.QUEUED);
            logger.log(order);
            Thread.sleep(500);
        }

        logger.log("No more new orders are accepted.");
        orderQueue.stop();

        while (System.currentTimeMillis() - startTime < config.getShutdownDeadline()) {
            if (orderQueue.size() == 0 && warehouse.size() == 0) {
                break;
            }
            Thread.sleep(1000);
        }

        logger.log("Pizza.Pizzeria is shutting down.");
        bakers.forEach(Baker::stop);
        couriers.forEach(Courier::stop);
        warehouse.stop();

        executor.shutdownNow();
        if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
            logger.logError("Executor did not terminate in the specified time.");
        }

        backupManager.serializeOrders(orderQueue, warehouse, bakers, couriers);

        logger.log("Pizza.Pizzeria has been shut down.");
    }
}
