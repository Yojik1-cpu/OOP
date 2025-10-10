package expressions.binary;

import expressions.Expression;
import expressions.atomic.Number;
import expressions.atomic.Variable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AddTest {
    @Test
    public void testAddStoresExpressions() {
        Expression left = new Number(3);
        Expression right = new Number(5);
        Add add = new Add(left, right);
        assertEquals(8.0, add.eval(""));
    }

    @Test
    public void testEvalTwoNumbers() {
        Add add = new Add(new Number(3), new Number(5));
        assertEquals(8.0, add.eval(""));
    }

    @Test
    public void testEvalNumberAndVariable() {
        Add add = new Add(new Number(3), new Variable("x"));
        assertEquals(13.0, add.eval("x=10"));
    }

    @Test
    public void testEvalVariableAndNumber() {
        Add add = new Add(new Variable("x"), new Number(5));
        assertEquals(15.0, add.eval("x=10"));
    }

    @Test
    public void testEvalTwoVariables() {
        Add add = new Add(new Variable("x"), new Variable("y"));
        assertEquals(25.0, add.eval("x=10; y=15"));
    }

    @Test
    public void testEvalDoubleAdd() {
        Add inner = new Add(new Number(2), new Number(3));
        Add outer = new Add(inner, new Number(4));
        assertEquals(9.0, outer.eval(""));
    }

    @Test
    public void testEvalNegativeNumbers() {
        Add add = new Add(new Number(-5), new Number(3));
        assertEquals(-2.0, add.eval(""));
    }

    @Test
    public void testEvalDecimalNumbers() {
        Add add = new Add(new Number(2.5), new Number(3.7));
        assertEquals(6.2, add.eval(""));
    }

    @Test
    public void testEvalNullVariablesString() {
        Add add = new Add(new Number(3), new Number(4));
        assertEquals(7.0, add.eval(null));
    }

    @Test
    public void testEvalEmptyVariablesString() {
        Add add = new Add(new Number(3), new Number(4));
        assertEquals(7.0, add.eval(""));
    }

    @Test
    public void testDerivativeTwoConstants() {
        Add add = new Add(new Number(5), new Number(3));
        Expression derivative = add.derivative("x");
        assertEquals(0.0, derivative.eval(""));
    }

    @Test
    public void testDerivativeConstantAndVariable() {
        Add add = new Add(new Number(5), new Variable("x"));
        Expression derivative = add.derivative("x");
        assertEquals(1.0, derivative.eval(""));
    }

    @Test
    public void testDerivativeVariableAndConstant() {
        Add add = new Add(new Variable("x"), new Number(5));
        Expression derivative = add.derivative("x");
        assertEquals(1.0, derivative.eval(""));
    }

    @Test
    public void testDerivativeTwoSameVariables() {
        Add add = new Add(new Variable("x"), new Variable("x"));
        Expression derivative = add.derivative("x");
        assertEquals(2.0, derivative.eval(""));
    }

    @Test
    public void testDerivativeTwoDifferentVariables() {
        Add add = new Add(new Variable("x"), new Variable("y"));
        Expression derivative = add.derivative("x");
        assertEquals(1.0, derivative.eval(""));
    }

    @Test
    public void testDerivativeRespectToNonExistentVariable() {
        Add add = new Add(new Variable("x"), new Variable("y"));
        Expression derivative = add.derivative("z");
        assertEquals(0.0, derivative.eval(""));
    }

    @Test
    public void testDerivativeNestedExpression() {
        Expression twoX = new expressions.binary.Mul(new Number(2), new Variable("x"));
        Add add = new Add(twoX, new Number(3));
        Expression derivative = add.derivative("x");
        assertNotNull(derivative);
    }

    @Test
    public void testToStringTwoNumbers() {
        Add add = new Add(new Number(3), new Number(5));
        assertEquals("(3 + 5)", add.toString());
    }

    @Test
    public void testToStringNumberAndVariable() {
        Add add = new Add(new Number(3), new Variable("x"));
        assertEquals("(3 + x)", add.toString());
    }

    @Test
    public void testToStringVariableAndNumber() {
        Add add = new Add(new Variable("x"), new Number(5));
        assertEquals("(x + 5)", add.toString());
    }

    @Test
    public void testToStringTwoVariables() {
        Add add = new Add(new Variable("x"), new Variable("y"));
        assertEquals("(x + y)", add.toString());
    }

    @Test
    public void testToStringNestedExpression() {
        Add inner = new Add(new Number(2), new Number(3));
        Add outer = new Add(inner, new Number(4));
        assertEquals("((2 + 3) + 4)", outer.toString());
    }

    @Test
    public void testToStringNegativeNumbers() {
        Add add = new Add(new Number(-5), new Number(3));
        assertEquals("(-5 + 3)", add.toString());
    }

}