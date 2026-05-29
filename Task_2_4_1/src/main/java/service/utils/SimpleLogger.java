package service.utils;

public class SimpleLogger {
    public static void info(String message) {
        System.out.println("[INFO] " + message);
    }

    public static void error(String message) {
        System.err.println("[ERROR] " + message);
    }

    public static void error(String message, Throwable t) {
        System.err.println("[ERROR] " + message);
        t.printStackTrace(System.err);
    }

    public static void warn(String message) {
        System.out.println("[WARN] " + message);
    }
}
