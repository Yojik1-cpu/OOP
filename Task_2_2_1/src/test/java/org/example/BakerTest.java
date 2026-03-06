package org.example;

import PizzeriaCore.Baker;
import PizzeriaCore.PizzaOrder;
import PizzeriaCore.SharedQueue;
import PizzeriaCore.Warehouse;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BakerTest {

    @Test
    void testBakerCooksOrder() throws InterruptedException {
        SharedQueue<PizzaOrder> orderQueue = new SharedQueue<>(5);
        Warehouse warehouse = new Warehouse(5);
        Baker baker = new Baker(1, 100, orderQueue, warehouse);

        PizzaOrder order = new PizzaOrder(1);
        orderQueue.add(order);

        Thread bakerThread = new Thread(baker);
        bakerThread.start();

        Thread.sleep(200); // Wait for baker to finish cooking

        assertEquals(1, warehouse.size());
        PizzaOrder cookedOrder = warehouse.takeOrders(1).get(0);
        assertEquals(PizzaOrder.State.WAREHOUSE, cookedOrder.getState());
        assertNull(baker.getCurrentOrder()); // Baker should not hold the order anymore

        baker.stop();
        bakerThread.join();
    }

    @Test
    void testBakerInterruptedWhileCooking() throws InterruptedException {
        SharedQueue<PizzaOrder> orderQueue = new SharedQueue<>(5);
        Warehouse warehouse = new Warehouse(5);
        // Use a long cooking time to ensure we can interrupt it
        Baker baker = new Baker(1, 5000, orderQueue, warehouse);

        PizzaOrder order = new PizzaOrder(1);
        orderQueue.add(order);

        Thread bakerThread = new Thread(baker);
        bakerThread.start();

        Thread.sleep(100); // Give baker time to start cooking
        assertEquals(PizzaOrder.State.COOKING, baker.getCurrentOrder().getState());

        bakerThread.interrupt(); // Interrupt the baker
        bakerThread.join(100); // Wait for thread to die

        assertNotNull(baker.getCurrentOrder(), "Baker should still hold the interrupted order");
        assertEquals(0, warehouse.size(), "Warehouse should be empty");
    }
}
