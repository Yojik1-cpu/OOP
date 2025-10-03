package expressions.binary;

import expressions.Expression;
import java.util.Map;

public class Sub extends Expression {
    private final Expression right;
    private final Expression left;

    public Sub(Expression left, Expression right) {
        this.right = right;
        this.left = left;
    }

    public double eval(String variables) {
        return left.eval(variables) - right.eval(variables);
    }

    @Override
    public String toString() {
        return "(" + left + " - " + right + ")";
    }
}
