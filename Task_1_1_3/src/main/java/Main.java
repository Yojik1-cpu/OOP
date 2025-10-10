import expressions.Expression;
import expressions.atomic.Number;
import expressions.atomic.Variable;
import expressions.binary.Add;
import expressions.binary.Div;
import expressions.binary.Mul;
import expressions.binary.Sub;

public class Main {
    public static void main(String[] args) {
        Expression e = new Div(
                new Add(new Number(3), new Mul(
                                                new Number(2), new Variable("x"))),
                new Sub(new Number(25), new Mul(
                                                new Variable("x"), new Variable("x"))));
        double result = e.eval("x = 10; y = 13");
        System.out.println(result);
        e.print();

        Expression de = e.derivative("x");
        de.print();
    }
}