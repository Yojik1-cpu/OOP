package expressions.binary;

import expressions.Expression;

public class Mul extends Expression {
    private final Expression right;
    private final Expression left;

    public Mul(Expression left, Expression right) {
        this.right = right;
        this.left = left;
    }

    public double eval(String variables) {
        return left.eval(variables) * right.eval(variables);
    }

    public Expression derivative(String variable) {
        return new Add(
                new Mul(left.derivative(variable), right),
                new Mul(left, right.derivative(variable))
        );
    }

    @Override
    public String toString() {
        return "(" + left + " * " + right + ")";
    }
}
