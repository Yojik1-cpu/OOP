package PizzeriaCore;

import java.io.Serializable;

public class PizzaOrder implements Serializable {
    private final int id;
    private State state;

    public PizzaOrder(int id) {
        this.id = id;
        this.state = State.QUEUED;
    }

    public int getId() {
        return id;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public enum State {
        QUEUED,
        COOKING,
        WAREHOUSE,
        DELIVERING,
        DELIVERED
    }
}
