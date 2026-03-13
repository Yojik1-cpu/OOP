package PizzeriaCore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Warehouse {
    private final SharedQueue<PizzaOrder> queue;
    private final OrderLogger logger;

    public Warehouse(int capacity, OrderLogger logger) {
        this.queue = new SharedQueue<>(capacity);
        this.logger = logger;
    }

    public void placeOrder(PizzaOrder order) throws InterruptedException {
        queue.add(order);
        if (order != null) {
            order.setState(PizzaOrder.State.WAREHOUSE);
            logger.log(order);
        }
    }

    public List<PizzaOrder> takeOrders(int count) throws InterruptedException {
        List<PizzaOrder> orders = new ArrayList<>();
        PizzaOrder firstOrder = queue.take();
        if (firstOrder == null) return orders;
        orders.add(firstOrder);
        for (int i = 1; i < count; i++) {
            PizzaOrder nextOrder = queue.poll();
            if (nextOrder == null) {
                break;
            }
            orders.add(nextOrder);
        }
        return orders;
    }

    public int size() {
        return queue.size();
    }

    public void stop() {
        queue.stop();
    }

    public void drainTo(Collection<PizzaOrder> collection) {
        queue.drainTo(collection);
    }
}
