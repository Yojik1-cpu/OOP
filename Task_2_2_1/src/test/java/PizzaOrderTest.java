import PizzeriaCore.PizzaOrder;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PizzaOrderTest {

    @Test
    void testInitialState() {
        PizzaOrder order = new PizzaOrder(1);
        assertEquals(1, order.getId());
        assertEquals(PizzaOrder.State.QUEUED, order.getState());
    }

    @Test
    void testStateTransitions() {
        PizzaOrder order = new PizzaOrder(1);
        
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
