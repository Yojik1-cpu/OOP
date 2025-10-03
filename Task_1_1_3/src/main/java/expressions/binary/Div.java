package expressions.binary;

import expressions.Expression;
import java.util.Map;

public class Div extends Expression {
    private final Expression right;
    private final Expression left;

    public Div(Expression left, Expression right) {
        this.right = right;
        this.left = left;
    }

    public double eval(String variables) {
        return left.eval(variables) / right.eval(variables);
    }

    @Override
    public String toString() {
        return "(" + left + " / " + right + ")";
    }
}
