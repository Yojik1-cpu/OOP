package expressions.binary;

import expressions.Expression;
import expressions.atomic.Number;
import expressions.atomic.Variable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class DivTest {

    @Test
    public void testDivStoresExpressions() {
        Expression left = new Number(3);
        Expression right = new Number(5);
        Div div = new Div(left, right);
        assertEquals(0.6, div.eval(""));
    }

    @Test
    public void testEvalTwoNumbers() {
        Div div = new Div(new Number(10), new Number(2));
        assertEquals(5.0, div.eval(""));
    }

    @Test
    public void testEvalNumberAndVariable() {
        Div div = new Div(new Number(20), new Variable("x"));
        assertEquals(4.0, div.eval("x=5"));
    }

    @Test
    public void testEvalVariableAndNumber() {
        Div div = new Div(new Variable("x"), new Number(4));
        assertEquals(3.0, div.eval("x=12"));
    }

    @Test
    public void testEvalTwoVariables() {
        Div div = new Div(new Variable("x"), new Variable("y"));
        assertEquals(2.0, div.eval("x=10; y=5"));
    }

    @Test
    public void testEvalNestedExpression() {
        Div inner = new Div(new Number(12), new Number(3));
        Div outer = new Div(inner, new Number(2));
        assertEquals(2.0, outer.eval(""));
    }

    @Test
    public void testEvalNegativeNumbers() {
        Div div = new Div(new Number(-10), new Number(2));
        assertEquals(-5.0, div.eval(""));
    }

    @Test
    public void testEvalDecimalNumbers() {
        Div div = new Div(new Number(5), new Number(2));
        assertEquals(2.5, div.eval(""));
    }

    @Test
    public void testEvalNullVariablesString() {
        Div div = new Div(new Number(10), new Number(2));
        assertEquals(5.0, div.eval(null));
    }

    @Test
    public void testEvalEmptyVariablesString() {
        Div div = new Div(new Number(10), new Number(2));
        assertEquals(5.0, div.eval(""));
    }

    @Test
    public void testDerivativeTwoConstants() {
        Div div = new Div(new Number(6), new Number(2));
        Expression derivative = div.derivative("x");
        assertEquals(0.0, derivative.eval(""));
    }

    @Test
    public void testDerivativeConstantAndVariable() {
        Div div = new Div(new Number(5), new Variable("x"));
        Expression derivative = div.derivative("x");
        assertEquals(-0.2, derivative.eval("x=5"));
    }

    @Test
    public void testDerivativeVariableAndConstant() {
        Div div = new Div(new Variable("x"), new Number(5));
        Expression derivative = div.derivative("x");
        assertEquals(0.2, derivative.eval("x=10"));
    }

    @Test
    public void testDerivativeSameVariables() {
        Div div = new Div(new Variable("x"), new Variable("x"));
        Expression derivative = div.derivative("x");
        assertEquals(0.0, derivative.eval("x=5"));
    }

    @Test
    public void testDerivativeDifferentVariables() {
        Div div = new Div(new Variable("x"), new Variable("y"));
        Expression derivative = div.derivative("x");
        assertEquals(0.25, derivative.eval("x=1; y=4"));
    }

    @Test
    public void testDerivativeNonExistentVariable() {
        Div div = new Div(new Variable("x"), new Variable("y"));
        Expression derivative = div.derivative("z");
        assertEquals(0.0, derivative.eval("x=1; y=4"));
    }

    @Test
    public void testDerivativeNestedExpression() {
        Expression twoX = new Mul(new Number(2), new Variable("x"));
        Div div = new Div(twoX, new Number(3));
        Expression derivative = div.derivative("x");
        assertEquals(0.666, derivative.eval("x=1"), 0.001);
    }

    @Test
    public void testToStringTwoNumbers() {
        Div div = new Div(new Number(10), new Number(2));
        assertEquals("(10 / 2)", div.toString());
    }

    @Test
    public void testToStringNumberAndVariable() {
        Div div = new Div(new Number(5), new Variable("x"));
        assertEquals("(5 / x)", div.toString());
    }

    @Test
    public void testToStringVariableAndNumber() {
        Div div = new Div(new Variable("x"), new Number(3));
        assertEquals("(x / 3)", div.toString());
    }

    @Test
    public void testToStringTwoVariables() {
        Div div = new Div(new Variable("x"), new Variable("y"));
        assertEquals("(x / y)", div.toString());
    }

    @Test
    public void testToStringNestedExpression() {
        Div inner = new Div(new Number(8), new Number(2));
        Div outer = new Div(inner, new Number(2));
        assertEquals("((8 / 2) / 2)", outer.toString());
    }

    @Test
    public void testToStringNegativeNumbers() {
        Div div = new Div(new Number(-10), new Number(2));
        assertEquals("(-10 / 2)", div.toString());
    }
}