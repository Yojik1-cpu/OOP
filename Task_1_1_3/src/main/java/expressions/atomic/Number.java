package expressions.atomic;

import expressions.Expression;

public class Number extends Expression {
    private final double value;

    public Number(double value) {
        this.value = value;
    }

    public double eval(String variablesString){
        return value;
    }

    /** дописать
    public String devirative(Variable variable){
        ...
    }
    */

    @Override
    public String toString() {
        if (value == (int) value) {
            return Integer.toString((int) value);
        }
        return Double.toString(value);
    }
}
