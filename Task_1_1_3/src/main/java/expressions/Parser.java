package expressions;

import expressions.atomic.Number;
import expressions.atomic.Variable;
import expressions.binary.Add;
import expressions.binary.Div;
import expressions.binary.Mul;
import expressions.binary.Sub;
import expressions.exceptions.ParseException;

public class Parser {
    private final String s;
    private int pos;

    private Parser(String s) {
        this.s = s;
        this.pos = 0;
    }

    public static Expression parse(String input) throws ParseException {
        Parser parser = new Parser(input);
        Expression expr = parser.parseExpression();
        parser.skipSpaces();
        if (!parser.isEnd()) {
            throw new ParseException("Unexpected characters at end: " + input.substring(parser.pos));
        }
        return expr;
    }


    private Expression parseExpression() throws ParseException {
        skipSpaces();
        if (isEnd()) {
            throw new ParseException("Unexpected end of expression");
        }

        char c = peek();

        if (c == '(') {
            consume('(');
            Expression left = parseExpression();
            skipSpaces();
            char op = next();
            skipSpaces();
            Expression right = parseExpression();
            skipSpaces();
            consume(')');

            return switch (op) {
                case '+' -> new Add(left, right);
                case '-' -> new Sub(left, right);
                case '*' -> new Mul(left, right);
                case '/' -> new Div(left, right);
                default -> throw new ParseException("Unknown operator: " + op);
            };
        } else {
            if (isDigit(c) || c == '-') {
                return parseNumber();
            } else if (isLetter(c)) {
                return parseVariable();
            } else {
                throw new ParseException("Unexpected character: " + c + " at pos " + pos);
            }
        }
    }

    private Number parseNumber() {
        skipSpaces();
        int start = pos;
        if (peek() == '-') {
            pos++;
        }
        while (!isEnd() && isDigit(peek())) {
            pos++;
        }
        if (!isEnd() && peek() == '.') {
            pos++;
            while (!isEnd() && isDigit(peek())) {
                pos++;
            }
        }
        String numStr = s.substring(start, pos);
        double value = Double.parseDouble(numStr);
        return new Number(value);
    }

    private Variable parseVariable() {
        skipSpaces();
        int start = pos;
        while (!isEnd() && (isLetter(peek()) || isDigit(peek()) || peek() == '_')) {
            pos++;
        }
        String name = s.substring(start, pos);
        return new Variable(name);
    }

    private void skipSpaces() {
        while (!isEnd() && Character.isWhitespace(peek())) {
            pos++;
        }
    }

    private boolean isEnd() {
        return pos >= s.length();
    }

    private char peek() {
        return s.charAt(pos);
    }

    private char next() {
        return s.charAt(pos++);
    }

    private void consume(char expected) throws ParseException {
        skipSpaces();
        if (isEnd() || peek() != expected) {
            throw new ParseException("Expected '" + expected + "' at pos " + pos);
        }
        pos++;
    }

    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private static boolean isLetter(char c) {
        return Character.isLetter(c);
    }
}
