import PizzeriaCore.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CourierTest {

    @Test
    void testCourierRun() throws InterruptedException {
        OrderLogger logger = new OrderLogger();
        Warehouse warehouse = new Warehouse(10, logger);
        SharedQueue<PizzaOrder> orderQueue = new SharedQueue<>(10);
        Courier courier = new Courier(1, 2, warehouse, logger);
        
        PizzaOrder order1 = new PizzaOrder(1);
        warehouse.placeOrder(order1);
        
        Thread courierThread = new Thread(courier);
        courierThread.start();
        
        Thread.sleep(1500);
        
        courier.stop();
        courierThread.join(1000);
        
        assertEquals(PizzaOrder.State.DELIVERED, order1.getState());
        assertEquals(0, warehouse.size());
    }

    @Test
    void testCourierStop() throws InterruptedException {
        OrderLogger logger = new OrderLogger();
        Warehouse warehouse = new Warehouse(10, logger);
        Courier courier = new Courier(1, 2, warehouse, logger);
        
        Thread courierThread = new Thread(courier);
        courierThread.start();
        
        courier.stop();
        courierThread.join(1000);
        
        assertFalse(courierThread.isAlive());
    }

    @Test
    void testCourierCurrentOrders() throws InterruptedException {
        OrderLogger logger = new OrderLogger();
        Warehouse warehouse = new Warehouse(10, logger);
        Courier courier = new Courier(1, 2, warehouse, logger);
        
        PizzaOrder order1 = new PizzaOrder(1);
        warehouse.placeOrder(order1);
        
        Thread courierThread = new Thread(courier);
        courierThread.start();
        
        Thread.sleep(100);
        
        List<PizzaOrder> currentOrders = courier.getCurrentOrders();
        assertNotNull(currentOrders);
        assertEquals(1, currentOrders.size());
        assertEquals(order1, currentOrders.get(0));
        assertEquals(PizzaOrder.State.DELIVERING, order1.getState());
        
        courier.stop();
        courierThread.join();
    }
}
