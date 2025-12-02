import expressions.Expression;
import expressions.Parser;
import expressions.exceptions.ParseException;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter an expression: ");
        String exprString = scanner.nextLine().trim();

        Expression e;
        try {
            e = Parser.parse(exprString);
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }

        String vars;
        double result;

        while (true) {
            System.out.println("Enter variable assignments in the format: x = 10; y = 13");
            vars = scanner.nextLine().trim();

            try {
                result = e.eval(vars);
                break;
            } catch (Exception ex) {
                System.out.println("Error while evaluating expression: " + ex.getMessage());
                System.out.println("Please try again.\n");
            }
        }

        System.out.println("Value of the expression: " + result);
        System.out.print("Expression: ");
        e.print();

        System.out.println("Enter the variable to differentiate with respect to (e.g., x):");
        String varToDiff = scanner.nextLine().trim();

        Expression de = e.derivative(varToDiff);
        System.out.print("Derivative with respect to " + varToDiff + ": ");
        de.print();
    }
}
