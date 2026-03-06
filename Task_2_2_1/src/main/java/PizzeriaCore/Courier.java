package PizzeriaCore;

import java.util.List;

public class Courier implements Runnable {
    private final int id;
    private final int trunkCapacity;
    private final Warehouse warehouse;
    private volatile boolean running = true;
    private volatile List<PizzaOrder> currentOrders;

    public Courier(int id, int trunkCapacity, Warehouse warehouse) {
        this.id = id;
        this.trunkCapacity = trunkCapacity;
        this.warehouse = warehouse;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("Courier-" + id);
        while (running) {
            try {
                List<PizzaOrder> orders = warehouse.takeOrders(trunkCapacity);
                if (orders.isEmpty()) {
                    if (!running) {
                        break;
                    }
                    continue;
                }
                currentOrders = orders;
                for (PizzaOrder order : orders) {
                    order.setState(PizzaOrder.State.DELIVERING);
                }
                Thread.sleep(1000);
                for (PizzaOrder order : orders) {
                    order.setState(PizzaOrder.State.DELIVERED);
                }
                currentOrders = null;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void stop() {
        running = false;
    }

    public List<PizzaOrder> getCurrentOrders() {
        return currentOrders;
    }
}
