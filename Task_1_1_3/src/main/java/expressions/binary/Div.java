package expressions.binary;

import expressions.Expression;
import expressions.exceptions.DivisionByZeroException;

public class Div extends Expression {
    private final Expression right;
    private final Expression left;

    public Div(Expression left, Expression right) {
        this.right = right;
        this.left = left;
    }

    public double eval(String variables) {
        double denominator = right.eval(variables);

        if (denominator == 0.0) {
            throw new DivisionByZeroException("Division by zero");
        }

        double numerator = left.eval(variables);
        return numerator / denominator;
    }

    public Expression derivative(String variable) {
        return new Div(
                new Sub(
                        new Mul(left.derivative(variable), right),
                        new Mul(left, right.derivative(variable))),
                new Mul(right, right));
    }

    @Override
    public String toString() {
        return "(" + left + " / " + right + ")";
    }
}
