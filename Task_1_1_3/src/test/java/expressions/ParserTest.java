package expressions;

import static org.junit.jupiter.api.Assertions.*;

import expressions.exceptions.ParseException;
import org.junit.jupiter.api.Test;

class ParserTest {

    @Test
    void parseSimpleIntegerNumber() throws ParseException {
        Expression expr = Parser.parse("42");
        assertEquals(42.0, expr.eval(""));
    }

    @Test
    void parseNegativeNumber() throws ParseException {
        Expression expr = Parser.parse("-7");
        assertEquals(-7.0, expr.eval(""));
    }

    @Test
    void parseDoubleNumber() throws ParseException {
        Expression expr = Parser.parse("3.5");
        assertEquals(3.5, expr.eval(""));
    }

    @Test
    void parseVariableSingleLetter() throws ParseException {
        Expression expr = Parser.parse("x");
        assertEquals(10.0, expr.eval("x = 10"));
    }

    @Test
    void parseVariableMultiLetterWithDigits() throws ParseException {
        Expression expr = Parser.parse("longName_123456789");
        assertEquals(5.0, expr.eval("longName_123456789 = 5; x = 10"));
    }

    @Test
    void parseSimpleAdd() throws ParseException {
        Expression expr = Parser.parse("(1+2)");
        assertEquals(3.0, expr.eval(""));
    }

    @Test
    void parseSimpleSub() throws ParseException {
        Expression expr = Parser.parse("(5-3)");
        assertEquals(2.0, expr.eval(""));
    }

    @Test
    void parseSimpleMul() throws ParseException {
        Expression expr = Parser.parse("(4*3)");
        assertEquals(12.0, expr.eval(""));
    }

    @Test
    void parseSimpleDiv() throws ParseException {
        Expression expr = Parser.parse("(8/2)");
        assertEquals(4.0, expr.eval(""));
    }

    @Test
    void parseNestedExpressionWithSpaces() throws ParseException {
        Expression expr = Parser.parse(" ( ( x + 3 ) * ( 2 - 5 ) ) ");
        assertEquals(-39.0, expr.eval("x = 10"));
    }

    @Test
    void parseExpressionFromTaskExample() throws ParseException {
        Expression expr = Parser.parse("(3+(2*x))");
        assertEquals(23.0, expr.eval("x = 10; y = 13"));
    }

    @Test
    void parseMoreComplexNestedExpression() throws ParseException {
        Expression expr = Parser.parse("((3+(2*x))/(25-(x*x)))");
        double val = expr.eval("x = 4");
        // = (3 + 8) / 9 = 11/9
        assertEquals(11.0 / 9.0, val);
    }

    @Test
    void parseUnexpectedCharactersAtEnd() {
        assertThrows(ParseException.class,
                () -> Parser.parse("(1+2)abc"));
    }

    @Test
    void parseUnexpectedCharacterAtStart() {
        assertThrows(ParseException.class,
                () -> Parser.parse("#(1+2)"));
    }

    @Test
    void parseUnknownOperator() {
        assertThrows(ParseException.class,
                () -> Parser.parse("(1$2)"));
    }

    @Test
    void parseMissingClosingParenthesis() {
        assertThrows(ParseException.class,
                () -> Parser.parse("(1+2"));
    }

    @Test
    void parseUnexpectedEndOfExpression() {
        assertThrows(ParseException.class,
                () -> Parser.parse(""));
    }
}
