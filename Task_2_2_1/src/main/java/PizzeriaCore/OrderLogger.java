package PizzeriaCore;

import java.io.Serializable;

public class OrderLogger implements Serializable {
    public void log(PizzaOrder order) {
        System.out.println("[" + order.getId() + "] " + order.getState());
    }

    public void log(String message) {
        System.out.println(message);
    }

    public void logError(String message) {
        System.err.println(message);
    }
}
