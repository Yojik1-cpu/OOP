package expressions.atomic;

import expressions.Expression;
import expressions.exceptions.UnknownVariableException;
import java.util.HashMap;
import java.util.Map;

public class Variable extends Expression {

    private final String name;
    private static String cachedInput = null;
    private static Map<String, Double> cachedVars = null;

    public Variable(String name) {
        this.name = name;
    }

    @Override
    public double eval(String variablesString) {
        Map<String, Double> vars = parseVariables(variablesString);

        if (!vars.containsKey(name)) {
            throw new UnknownVariableException("Unknown variable: " + name);
        }

        return vars.get(name);
    }

    public static Map<String, Double> parseVariables(String variablesString) {
        if (cachedInput != null && cachedInput.equals(variablesString)) {
            return cachedVars;
        }

        Map<String, Double> variables = new HashMap<>();

        if (variablesString == null || variablesString.trim().isEmpty()) {
            cachedInput = variablesString;
            cachedVars = variables;
            return variables;
        }

        String[] assignments = variablesString.split(";");

        for (String assignment : assignments) {
            assignment = assignment.trim();
            if (assignment.isEmpty()) continue;

            String[] parts = assignment.split("=");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid variable assignment: " + assignment);
            }

            String varName = parts[0].trim();
            String varValueStr = parts[1].trim();

            double varValue;
            try {
                varValue = Double.parseDouble(varValueStr);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format: " + varValueStr);
            }

            variables.put(varName, varValue);
        }

        cachedInput = variablesString;
        cachedVars = variables;

        return variables;
    }

    @Override
    public Expression derivative(String variable) {
        return new Number(name.equals(variable) ? 1 : 0);
    }

    @Override
    public String toString() {
        return name;
    }
}
