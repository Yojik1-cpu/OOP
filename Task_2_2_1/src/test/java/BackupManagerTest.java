import PizzeriaCore.*;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BackupManagerTest {

    @Test
    void testSerialization() throws IOException, InterruptedException {
        OrderLogger logger = new OrderLogger();
        BackupManager backupManager = new BackupManager(logger);
        SharedQueue<PizzaOrder> orderQueue = new SharedQueue<>(10);
        Warehouse warehouse = new Warehouse(10, logger);
        List<Baker> bakers = new ArrayList<>();
        List<Courier> couriers = new ArrayList<>();
        
        PizzaOrder order1 = new PizzaOrder(1);
        orderQueue.add(order1);
        
        PizzaOrder order2 = new PizzaOrder(2);
        warehouse.placeOrder(order2);
        
        backupManager.serializeOrders(orderQueue, warehouse, bakers, couriers);
        
        SharedQueue<PizzaOrder> restoredQueue = new SharedQueue<>(10);
        int maxId = backupManager.deserializeOrders(restoredQueue);
        
        assertEquals(2, maxId);
        assertEquals(2, restoredQueue.size());
    }
}
