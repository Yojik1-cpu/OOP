package expressions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import expressions.atomic.Number;
import expressions.atomic.Variable;
import expressions.binary.Add;
import expressions.binary.Mul;
import org.junit.jupiter.api.Test;

class ExpressionTest {
    @Test
    public void testToStringNotNull() {
        Expression[] expressions = {
            new Number(5),
            new Variable("x"),
            new Add(new Number(2), new Number(3)),
            new Mul(new Number(2), new Variable("x"))
        };

        for (Expression expr : expressions) {
            assertNotNull(expr.toString());
        }
    }

    @Test
    public void testDerivativeReturnsExpressionForAllTypes() {
        Expression[] expressions = {
            new Number(5),
            new Variable("x"),
            new Add(new Number(2), new Number(3)),
            new Mul(new Number(2), new Variable("x"))
        };

        for (Expression expr : expressions) {
            Expression derivative = expr.derivative("x");
            assertNotNull(derivative);
        }
    }

    @Test
    public void testDerivativeOfConstantIsZero() {
        Expression constant = new Number(5);
        Expression derivative = constant.derivative("x");
        assertEquals(0.0, derivative.eval("x=10"));
    }

    @Test
    public void testCommutativePropertyForAdd() {
        Expression a = new Number(3);
        Expression b = new Variable("x");
        Add ab = new Add(a, b);
        Add ba = new Add(b, a);
        assertEquals(ab.eval("x=5"), ba.eval("x=5"));
    }

    @Test
    public void testZeroPropertyForAdd() {
        Expression a = new Variable("x");
        Expression zero = new Number(0);
        Add plusZero = new Add(a, zero);
        assertEquals(10.0, plusZero.eval("x=10"));
    }

    @Test
    public void testNestedExpressionsEvaluation() {
        Expression inner = new Add(new Number(2), new Variable("x"));
        Expression outer = new Mul(inner, new Number(3));
        assertEquals(21.0, outer.eval("x=5"));
    }

    @Test
    public void testExpressionImmutability() {
        Expression expr = new Add(new Number(2), new Variable("x"));
        double firstResult = expr.eval("x=5");
        double secondResult = expr.eval("x=5");
        assertEquals(firstResult, secondResult);
    }
}