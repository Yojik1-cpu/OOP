package PizzeriaCore;

public class Baker implements Runnable {
    private final int id;
    private final int cookingTime;
    private final SharedQueue<PizzaOrder> orderQueue;
    private final Warehouse warehouse;
    private volatile boolean running = true;
    private volatile PizzaOrder currentOrder;

    public Baker(int id, int cookingTime, SharedQueue<PizzaOrder> orderQueue, Warehouse warehouse) {
        this.id = id;
        this.cookingTime = cookingTime;
        this.orderQueue = orderQueue;
        this.warehouse = warehouse;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("Baker-" + id);
        while (running) {
            try {
                PizzaOrder order = orderQueue.take();
                if (order == null) {
                    break;
                }
                currentOrder = order;
                order.setState(PizzaOrder.State.COOKING);
                Thread.sleep(cookingTime);
                warehouse.placeOrder(order);
                currentOrder = null;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void stop() {
        running = false;
    }

    public PizzaOrder getCurrentOrder() {
        return currentOrder;
    }
}
