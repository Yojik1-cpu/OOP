package expressions.binary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import expressions.Expression;
import expressions.atomic.Number;
import expressions.atomic.Variable;
import org.junit.jupiter.api.Test;

class MulTest {

    @Test
    public void testMulStoresExpressions() {
        Expression left = new Number(3);
        Expression right = new Number(5);
        Mul mul = new Mul(left, right);
        assertEquals(15.0, mul.eval(""));
    }

    @Test
    public void testEvalTwoNumbers() {
        Mul mul = new Mul(new Number(3), new Number(5));
        assertEquals(15.0, mul.eval(""));
    }

    @Test
    public void testEvalNumberAndVariable() {
        Mul mul = new Mul(new Number(3), new Variable("x"));
        assertEquals(30.0, mul.eval("x=10"));
    }

    @Test
    public void testEvalVariableAndNumber() {
        Mul mul = new Mul(new Variable("x"), new Number(5));
        assertEquals(50.0, mul.eval("x=10"));
    }

    @Test
    public void testEvalTwoVariables() {
        Mul mul = new Mul(new Variable("x"), new Variable("y"));
        assertEquals(150.0, mul.eval("x=10; y=15"));
    }

    @Test
    public void testEvalDoubleMul() {
        Mul inner = new Mul(new Number(2), new Number(3));
        Mul outer = new Mul(inner, new Number(4));
        assertEquals(24.0, outer.eval(""));
    }

    @Test
    public void testEvalNegativeNumbers() {
        Mul mul = new Mul(new Number(-5), new Number(3));
        assertEquals(-15.0, mul.eval(""));
    }

    @Test
    public void testEvalDecimalNumbers() {
        Mul mul = new Mul(new Number(2.5), new Number(3.0));
        assertEquals(7.5, mul.eval(""));
    }

    @Test
    public void testEvalNullVariablesString() {
        Mul mul = new Mul(new Number(3), new Number(4));
        assertEquals(12.0, mul.eval(null));
    }

    @Test
    public void testEvalEmptyVariablesString() {
        Mul mul = new Mul(new Number(3), new Number(4));
        assertEquals(12.0, mul.eval(""));
    }

    @Test
    public void testDerivativeTwoConstants() {
        Mul mul = new Mul(new Number(5), new Number(3));
        Expression derivative = mul.derivative("x");
        assertEquals(0.0, derivative.eval(""));
    }

    @Test
    public void testDerivativeConstantAndVariable() {
        Mul mul = new Mul(new Number(5), new Variable("x"));
        Expression derivative = mul.derivative("x");
        assertEquals(5.0, derivative.eval("x=1"));
    }

    @Test
    public void testDerivativeVariableAndConstant() {
        Mul mul = new Mul(new Variable("x"), new Number(5));
        Expression derivative = mul.derivative("x");
        assertEquals(5.0, derivative.eval("x=1"));
    }

    @Test
    public void testDerivativeTwoSameVariables() {

        Mul mul = new Mul(new Variable("x"), new Variable("x"));
        Expression derivative = mul.derivative("x");
        assertEquals(20.0, derivative.eval("x=10"));
    }

    @Test
    public void testDerivativeTwoDifferentVariables() {
        Mul mul = new Mul(new Variable("x"), new Variable("y"));
        Expression derivative = mul.derivative("x");
        assertEquals(15.0, derivative.eval("x =1;y=15"));
    }

    @Test
    public void testDerivativeRespectNonExistentVariable() {
        Mul mul = new Mul(new Variable("x"), new Variable("y"));
        Expression derivative = mul.derivative("z");
        assertEquals(0.0, derivative.eval("x=10;y=123"));
    }

    @Test
    public void testDerivativeNestedExpression() {
        Expression twoX = new Mul(new Number(2), new Variable("x"));
        Mul mul = new Mul(twoX, new Number(3));
        Expression derivative = mul.derivative("x");
        assertNotNull(derivative);
    }

    @Test
    public void testToStringTwoNumbers() {
        Mul mul = new Mul(new Number(3), new Number(5));
        assertEquals("(3 * 5)", mul.toString());
    }

    @Test
    public void testToStringNumberAndVariable() {
        Mul mul = new Mul(new Number(3), new Variable("x"));
        assertEquals("(3 * x)", mul.toString());
    }

    @Test
    public void testToStringVariableAndNumber() {
        Mul mul = new Mul(new Variable("x"), new Number(5));
        assertEquals("(x * 5)", mul.toString());
    }

    @Test
    public void testToStringTwoVariables() {
        Mul mul = new Mul(new Variable("x"), new Variable("y"));
        assertEquals("(x * y)", mul.toString());
    }

    @Test
    public void testToStringNestedExpression() {
        Mul inner = new Mul(new Number(2), new Number(3));
        Mul outer = new Mul(inner, new Number(4));
        assertEquals("((2 * 3) * 4)", outer.toString());
    }

    @Test
    public void testToStringNegativeNumbers() {
        Mul mul = new Mul(new Number(-5), new Number(3));
        assertEquals("(-5 * 3)", mul.toString());
    }
}