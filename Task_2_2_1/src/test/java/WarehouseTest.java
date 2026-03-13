import PizzeriaCore.OrderLogger;
import PizzeriaCore.PizzaOrder;
import PizzeriaCore.Warehouse;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class WarehouseTest {

    @Test
    void testPlaceOrder() throws InterruptedException {
        OrderLogger logger = new OrderLogger();
        Warehouse warehouse = new Warehouse(10, logger);
        PizzaOrder order = new PizzaOrder(1);

        warehouse.placeOrder(order);

        assertEquals(PizzaOrder.State.WAREHOUSE, order.getState());
        assertEquals(1, warehouse.size());
    }

    @Test
    void testTakeOrders() throws InterruptedException {
        OrderLogger logger = new OrderLogger();
        Warehouse warehouse = new Warehouse(10, logger);
        PizzaOrder order1 = new PizzaOrder(1);
        PizzaOrder order2 = new PizzaOrder(2);

        warehouse.placeOrder(order1);
        warehouse.placeOrder(order2);

        List<PizzaOrder> orders = warehouse.takeOrders(2);
        assertEquals(2, orders.size());
        assertEquals(order1, orders.get(0));
        assertEquals(order2, orders.get(1));
    }

    @Test
    void testStop() throws InterruptedException {
        OrderLogger logger = new OrderLogger();
        Warehouse warehouse = new Warehouse(1, logger);
        
        Thread thread = new Thread(() -> {
            try {
                warehouse.takeOrders(1);
            } catch (InterruptedException e) {
            }
        });
        thread.start();
        
        warehouse.stop();
        thread.join(1000);
        
        assertFalse(thread.isAlive());
    }

    @Test
    void testDrainTo() throws InterruptedException {
        OrderLogger logger = new OrderLogger();
        Warehouse warehouse = new Warehouse(3, logger);
        PizzaOrder order = new PizzaOrder(1);
        
        warehouse.placeOrder(order);
        
        List<PizzaOrder> list = new ArrayList<>();
        warehouse.drainTo(list);
        
        assertEquals(1, list.size());
        assertEquals(order, list.get(0));
    }
}
