package PizzeriaCore;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PizzaOrderTest {
    @Test
    void testStateTransitions() {
        PizzaOrder order = new PizzaOrder(1);
        assertEquals(PizzaOrder.State.QUEUED, order.getState());

        order.setState(PizzaOrder.State.COOKING);
        assertEquals(PizzaOrder.State.COOKING, order.getState());

        order.setState(PizzaOrder.State.WAREHOUSE);
        assertEquals(PizzaOrder.State.WAREHOUSE, order.getState());

        order.setState(PizzaOrder.State.DELIVERING);
        assertEquals(PizzaOrder.State.DELIVERING, order.getState());

        order.setState(PizzaOrder.State.DELIVERED);
        assertEquals(PizzaOrder.State.DELIVERED, order.getState());
    }
}
