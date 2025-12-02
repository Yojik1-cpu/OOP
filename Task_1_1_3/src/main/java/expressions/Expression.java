package expressions;

public abstract class Expression {
    public abstract double eval(String variablesString);

    public abstract Expression derivative(String variable);

    public abstract String toString();

    public void print() {
        System.out.print(this + "\n");
    }
}
