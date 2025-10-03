package expressions;

import expressions.atomic.Variable;

import java.util.Map;

public abstract class Expression {
    public abstract double eval(String variablesString);

    //public abstract String devirative(Variable variable);

    public abstract String toString();

    public void print() {
        System.out.print(this.toString());
    }


}
