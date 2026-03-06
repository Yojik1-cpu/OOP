package org.example;

import PizzeriaCore.PizzaOrder;
import PizzeriaCore.Warehouse;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

public class WarehouseTest {

    @Test
    void testPlaceAndTake() throws InterruptedException {
        Warehouse warehouse = new Warehouse(5);
        PizzaOrder order1 = new PizzaOrder(1);
        PizzaOrder order2 = new PizzaOrder(2);

        warehouse.placeOrder(order1);
        warehouse.placeOrder(order2);

        assertEquals(2, warehouse.size());
        assertEquals(PizzaOrder.State.WAREHOUSE, order1.getState());

        List<PizzaOrder> orders = warehouse.takeOrders(2);
        assertEquals(2, orders.size());
        assertEquals(0, warehouse.size());
    }

    @Test
    void testTakePartial() throws InterruptedException {
        Warehouse warehouse = new Warehouse(5);
        warehouse.placeOrder(new PizzaOrder(1));

        List<PizzaOrder> orders = warehouse.takeOrders(2);
        assertEquals(1, orders.size());
    }

    @Test
    void testTakeBlocksWhenEmpty() throws InterruptedException {
        Warehouse warehouse = new Warehouse(5);
        CountDownLatch latch = new CountDownLatch(1);

        Thread consumer = new Thread(() -> {
            try {
                warehouse.takeOrders(1);
                latch.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        consumer.start();

        Thread.sleep(100);
        assertEquals(1, latch.getCount(), "Consumer should be blocked");

        warehouse.placeOrder(new PizzaOrder(1));
        assertTrue(latch.await(1, TimeUnit.SECONDS), "Consumer should have been unblocked");
        consumer.join();
    }

    @Test
    void testStopUnblocksTake() throws InterruptedException {
        Warehouse warehouse = new Warehouse(5);
        CountDownLatch latch = new CountDownLatch(1);

        Thread consumer = new Thread(() -> {
            try {
                List<PizzaOrder> orders = warehouse.takeOrders(1);
                assertTrue(orders.isEmpty());
                latch.countDown();
            } catch (InterruptedException e) {
                // This is expected
            }
        });
        consumer.start();

        Thread.sleep(100);
        warehouse.stop();

        assertTrue(latch.await(1, TimeUnit.SECONDS), "Consumer should have been unblocked by stop()");
        consumer.join();
    }

    @Test
    void testDrainTo() throws InterruptedException {
        Warehouse warehouse = new Warehouse(5);
        warehouse.placeOrder(new PizzaOrder(1));
        warehouse.placeOrder(new PizzaOrder(2));

        List<PizzaOrder> list = new ArrayList<>();
        warehouse.drainTo(list);

        assertEquals(2, list.size());
        assertEquals(0, warehouse.size());
    }
}
