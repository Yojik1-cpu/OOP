import expressions.atomic.Number;
import expressions.atomic.Variable;
import expressions.binary.Add;
import expressions.binary.Mul;
import expressions.Expression;

public class Main {
    public static void main(String[] args) {
        Expression e = new Add(new Number(3), new Mul(new Number(2),
                new Variable("x")));
        double result = e.eval("x = 10; y = 13");
        System.out.println(result);
        e.print();
    }
}