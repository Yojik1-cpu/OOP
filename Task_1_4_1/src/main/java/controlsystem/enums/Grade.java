package controlsystem.enums;

public enum Grade {
    EXCELLENT(5),
    GOOD(4),
    SATISFACTORY(3),
    UNSATISFACTORY(2),
    PASS(0),
    FAIL(0);

    private final int numeric;

    Grade(int numeric) {
        this.numeric = numeric;
    }

    public int getNumeric() {
        return numeric;
    }

    public boolean isNumeric() {
        return numeric > 0;
    }
}
