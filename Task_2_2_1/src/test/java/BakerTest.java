import PizzeriaCore.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class BakerTest {

    @Test
    void testBakerRun() throws InterruptedException {
        OrderLogger logger = new OrderLogger();
        SharedQueue<PizzaOrder> orderQueue = new SharedQueue<>(10);
        Warehouse warehouse = new Warehouse(10, logger);
        Baker baker = new Baker(1, 100, orderQueue, warehouse, logger);
        
        PizzaOrder order = new PizzaOrder(1);
        orderQueue.add(order);
        
        Thread bakerThread = new Thread(baker);
        bakerThread.start();
        
        Thread.sleep(200);
        
        baker.stop();
        bakerThread.join(1000);
        
        assertEquals(PizzaOrder.State.WAREHOUSE, order.getState());
        assertEquals(1, warehouse.size());
    }

    @Test
    void testBakerStop() throws InterruptedException {
        OrderLogger logger = new OrderLogger();
        SharedQueue<PizzaOrder> orderQueue = new SharedQueue<>(10);
        Warehouse warehouse = new Warehouse(10, logger);
        Baker baker = new Baker(1, 100, orderQueue, warehouse, logger);
        
        Thread bakerThread = new Thread(baker);
        bakerThread.start();
        
        baker.stop();
        bakerThread.join(1000);
        
        assertFalse(bakerThread.isAlive());
    }

    @Test
    void testBakerCurrentOrder() throws InterruptedException {
        OrderLogger logger = new OrderLogger();
        SharedQueue<PizzaOrder> orderQueue = new SharedQueue<>(10);
        Warehouse warehouse = new Warehouse(10, logger);
        Baker baker = new Baker(1, 1000, orderQueue, warehouse, logger);
        
        PizzaOrder order = new PizzaOrder(1);
        orderQueue.add(order);
        
        Thread bakerThread = new Thread(baker);
        bakerThread.start();
        
        Thread.sleep(100);
        
        assertEquals(order, baker.getCurrentOrder());
        assertEquals(PizzaOrder.State.COOKING, order.getState());
        
        baker.stop();
        bakerThread.join();
    }
}
