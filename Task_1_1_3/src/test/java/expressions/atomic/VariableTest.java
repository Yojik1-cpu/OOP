package expressions.atomic;

import expressions.Expression;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VariableTest {
    @Test
    public void testVariableStoresName() {
        Variable var = new Variable("x");
        assertEquals("x", var.toString());
    }

    @Test
    public void testEvalExistingVariable() {
        Variable var = new Variable("x");
        assertEquals(10.0, var.eval("x=10"));
    }

    @Test
    public void testEvalMultipleVariables() {
        Variable var = new Variable("y");
        assertEquals(15.0, var.eval("x=10; y=15"));
    }

    @Test
    public void testEvalSpaces() {
        Variable var = new Variable("x");
        assertEquals(5.0, var.eval("x = 5"));
    }

    @Test
    public void testEvalNegativeNumber() {
        Variable var = new Variable("x");
        assertEquals(-5.0, var.eval("x=-5"));
    }

    @Test
    public void testEvalDecimalNumber() {
        Variable var = new Variable("x");
        assertEquals(3.14, var.eval("x=3.14"));
    }

    @Test
    public void testDerivativeSameVariable() {
        Variable var = new Variable("x");
        Expression derivative = var.derivative("x");
        assertEquals(1.0, derivative.eval(""));
    }

    @Test
    public void testDerivativeDifferentVariable() {
        Variable var = new Variable("x");
        Expression derivative = var.derivative("y");
        assertEquals(0.0, derivative.eval(""));
    }

    @Test
    public void testToString() {
        Variable var = new Variable("testVar");
        assertEquals("testVar", var.toString());
    }

}