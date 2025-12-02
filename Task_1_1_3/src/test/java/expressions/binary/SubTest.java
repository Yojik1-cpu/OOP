package expressions.binary;

import static org.junit.jupiter.api.Assertions.assertEquals;

import expressions.Expression;
import expressions.atomic.Number;
import expressions.atomic.Variable;
import org.junit.jupiter.api.Test;

class SubTest {
    @Test
    public void testSubStoresExpressions() {
        Expression left = new Number(3);
        Expression right = new Number(5);
        Sub add = new Sub(left, right);
        assertEquals(-2, add.eval(""));
    }

    @Test
    public void testEvalTwoNumbers() {
        Sub sub = new Sub(new Number(3), new Number(5));
        assertEquals(-2.0, sub.eval(""));
    }

    @Test
    public void testEvalNumberAndVariable() {
        Sub sub = new Sub(new Number(3), new Variable("x"));
        assertEquals(-7.0, sub.eval("x=10"));
    }

    @Test
    public void testEvalVariableAndNumber() {
        Sub sub = new Sub(new Variable("x"), new Number(5));
        assertEquals(5.0, sub.eval("x=10"));
    }

    @Test
    public void testEvalTwoVariables() {
        Sub sub = new Sub(new Variable("x"), new Variable("y"));
        assertEquals(-5.0, sub.eval("x=10; y=15"));
    }

    @Test
    public void testEvalNestedExpression() {
        // (5 - 2) - 1
        Sub inner = new Sub(new Number(5), new Number(2));
        Sub outer = new Sub(inner, new Number(1));
        assertEquals(2.0, outer.eval(""));
    }

    @Test
    public void testEvalNegativeNumbers() {
        Sub sub = new Sub(new Number(-5), new Number(3));
        assertEquals(-8.0, sub.eval(""));
    }

    @Test
    public void testEvalDecimalNumbers() {
        Sub sub = new Sub(new Number(5.5), new Number(2.2));
        assertEquals(3.3, sub.eval(""));
    }

    @Test
    public void testEvalNullVariablesString() {
        Sub sub = new Sub(new Number(10), new Number(3));
        assertEquals(7.0, sub.eval(null));
    }

    @Test
    public void testEvalEmptyVariablesString() {
        Sub sub = new Sub(new Number(10), new Number(3));
        assertEquals(7.0, sub.eval(""));
    }

    @Test
    public void testDerivativeTwoConstants() {
        Sub sub = new Sub(new Number(5), new Number(3));
        Expression derivative = sub.derivative("x");
        assertEquals(0.0, derivative.eval(""));
    }

    @Test
    public void testDerivativeConstantAndVariable() {
        Sub sub = new Sub(new Number(5), new Variable("x"));
        Expression derivative = sub.derivative("x");
        assertEquals(-1.0, derivative.eval(""));
    }

    @Test
    public void testDerivativeVariableAndConstant() {
        Sub sub = new Sub(new Variable("x"), new Number(5));
        Expression derivative = sub.derivative("x");
        assertEquals(1.0, derivative.eval(""));
    }

    @Test
    public void testDerivativeSameVariables() {
        Sub sub = new Sub(new Variable("x"), new Variable("x"));
        Expression derivative = sub.derivative("x");
        assertEquals(0.0, derivative.eval(""));
    }

    @Test
    public void testDerivativeDifferentVariables() {
        Sub sub = new Sub(new Variable("x"), new Variable("y"));
        Expression derivative = sub.derivative("x");
        assertEquals(1.0, derivative.eval(""));
    }

    @Test
    public void testDerivativeNonExistentVariable() {
        Sub sub = new Sub(new Variable("x"), new Variable("y"));
        Expression derivative = sub.derivative("z");
        assertEquals(0.0, derivative.eval(""));
    }

    @Test
    public void testDerivativeNestedExpression() {
        Expression twoX = new Mul(new Number(2), new Variable("x"));
        Sub sub = new Sub(twoX, new Number(3));
        Expression derivative = sub.derivative("x");
        assertEquals(2.0, derivative.eval("x=1"));
    }

    @Test
    public void testToStringTwoNumbers() {
        Sub sub = new Sub(new Number(3), new Number(5));
        assertEquals("(3 - 5)", sub.toString());
    }

    @Test
    public void testToStringNumberAndVariable() {
        Sub sub = new Sub(new Number(3), new Variable("x"));
        assertEquals("(3 - x)", sub.toString());
    }

    @Test
    public void testToStringVariableAndNumber() {
        Sub sub = new Sub(new Variable("x"), new Number(5));
        assertEquals("(x - 5)", sub.toString());
    }

    @Test
    public void testToStringTwoVariables() {
        Sub sub = new Sub(new Variable("x"), new Variable("y"));
        assertEquals("(x - y)", sub.toString());
    }

    @Test
    public void testToStringNestedExpression() {
        Sub inner = new Sub(new Number(2), new Number(3));
        Sub outer = new Sub(inner, new Number(4));
        assertEquals("((2 - 3) - 4)", outer.toString());
    }

    @Test
    public void testToStringNegativeNumbers() {
        Sub sub = new Sub(new Number(-5), new Number(3));
        assertEquals("(-5 - 3)", sub.toString());
    }
}