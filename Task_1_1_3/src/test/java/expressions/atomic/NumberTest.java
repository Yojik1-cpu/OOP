package expressions.atomic;

import expressions.Expression;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NumberTest {

    @Test
    public void testNumberReturnsSameValue() {
        Number num = new Number(5.5);
        assertEquals(5.5, num.eval("x=10"));
        assertEquals(5.5, num.eval("y=20"));
        assertEquals(5.5, num.eval(null));
        assertEquals(5.5, num.eval(""));
    }

    @Test
    public void testEvalReturnsConstantValue() {
        Number num = new Number(3.14);
        double result = num.eval("x=10; y=20");
        assertEquals(3.14, result);
    }

    @Test
    public void testEvalNullString() {
        Number num = new Number(7.0);
        double result = num.eval(null);
        assertEquals(7.0, result);
    }

    @Test
    public void testEvalEmptyString() {
        Number num = new Number(7.0);
        double result = num.eval("");
        assertEquals(7.0, result);
    }

    @Test
    public void testDerivativeReturnsZero() {
        Number num = new Number(10.0);
        Expression derivative = num.derivative("x");
        assertTrue(derivative instanceof Number);
        assertEquals(0.0, derivative.eval("x=5"));
    }

    @Test
    public void testDerivativeDifferentVariables() {
        Number num = new Number(15.0);

        Expression der1 = num.derivative("x");
        Expression der2 = num.derivative("y");
        Expression der3 = num.derivative("any_variable");

        assertEquals(0.0, der1.eval(""));
        assertEquals(0.0, der2.eval(""));
        assertEquals(0.0, der3.eval(""));
    }

    @Test
    public void testToStringIntegerValue() {
        Number num = new Number(5.0);
        assertEquals("5", num.toString());
    }

    @Test
    public void testToStringDoubleValue() {
        Number num = new Number(5.7);
        assertEquals("5.7", num.toString());
    }

    @Test
    public void testToStringNegativeInteger() {
        Number num = new Number(-3.0);
        assertEquals("-3", num.toString());
    }

    @Test
    public void testToStringNegativeDouble() {
        Number num = new Number(-3.14);
        assertEquals("-3.14", num.toString());
    }

    @Test
    public void testToStringZero() {
        Number num = new Number(0.0);
        assertEquals("0", num.toString());
    }
}