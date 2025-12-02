package expressions.binary;

import expressions.Expression;

public class Add extends Expression {
    private final Expression right;
    private final Expression left;

    public Add(Expression left, Expression right) {
        this.right = right;
        this.left = left;
    }

    public double eval(String variables) {
        return left.eval(variables) + right.eval(variables);
    }

    public Expression derivative(String variable) {
        return new Add(left.derivative(variable), right.derivative(variable));
    }

    @Override
    public String toString() {
        return "(" + left + " + " + right + ")";
    }
}
