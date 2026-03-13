package PizzeriaConfig;

import java.util.List;

public class PizzeriaConfiguration {
    private final List<BakerConfig> bakers;
    private final List<CourierConfig> couriers;
    private final int warehouseCapacity;
    private final long newOrdersDeadline;
    private final long shutdownDeadline;

    public PizzeriaConfiguration(List<BakerConfig> bakers, List<CourierConfig> couriers, int warehouseCapacity,
                                 long newOrdersDeadline, long shutdownDeadline) {
        this.bakers = bakers;
        this.couriers = couriers;
        this.warehouseCapacity = warehouseCapacity;
        this.newOrdersDeadline = newOrdersDeadline;
        this.shutdownDeadline = shutdownDeadline;
    }

    public List<BakerConfig> getBakers() {
        return bakers;
    }

    public List<CourierConfig> getCouriers() {
        return couriers;
    }

    public int getWarehouseCapacity() {
        return warehouseCapacity;
    }

    public long getNewOrdersDeadline() {
        return newOrdersDeadline;
    }

    public long getShutdownDeadline() {
        return shutdownDeadline;
    }
}
