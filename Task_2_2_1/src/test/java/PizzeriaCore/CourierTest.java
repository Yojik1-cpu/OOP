package PizzeriaCore;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class CourierTest {

    @Test
    void testCourierDeliversOrder() throws InterruptedException {
        Warehouse warehouse = new Warehouse(5);
        Courier courier = new Courier(1, 2, warehouse);

        PizzaOrder order1 = new PizzaOrder(1);
        PizzaOrder order2 = new PizzaOrder(2);
        warehouse.placeOrder(order1);
        warehouse.placeOrder(order2);

        Thread courierThread = new Thread(courier);
        courierThread.start();

        Thread.sleep(200); // Give courier time to pick up orders
        assertEquals(0, warehouse.size(), "Courier should have taken the orders");
        assertNotNull(courier.getCurrentOrders());
        assertEquals(2, courier.getCurrentOrders().size());
        assertEquals(PizzaOrder.State.DELIVERING, courier.getCurrentOrders().get(0).getState());

        Thread.sleep(1000); // Wait for delivery
        assertNull(courier.getCurrentOrders(), "Courier should have delivered the orders");

        courier.stop();
        courierThread.join();
    }

    @Test
    void testCourierInterruptedWhileDelivering() throws InterruptedException {
        Warehouse warehouse = new Warehouse(5);
        // Use a long delivery time to ensure we can interrupt it
        Courier courier = new Courier(1, 2, warehouse);

        PizzaOrder order = new PizzaOrder(1);
        warehouse.placeOrder(order);

        Thread courierThread = new Thread(courier);
        courierThread.start();

        Thread.sleep(100); // Give courier time to pick up and start delivering
        assertEquals(PizzaOrder.State.DELIVERING, courier.getCurrentOrders().get(0).getState());

        courierThread.interrupt(); // Interrupt the courier
        courierThread.join(100); // Wait for thread to die

        assertNotNull(courier.getCurrentOrders(), "Courier should still hold the interrupted orders");
    }
}
