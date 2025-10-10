package expressions.atomic;

import expressions.Expression;
import java.util.HashMap;
import java.util.Map;

public class Variable extends Expression {
    private final String name;

    public Variable(String name) {
        this.name = name;
    }

    public double eval(String variablesString) {
        Map<String, Double> variables = parseVariables(variablesString);

        if (!variables.containsKey(name)) {
            throw new IllegalArgumentException("Unknown variable: " + name);
        }
        return variables.get(name);
    }

    private Map<String, Double> parseVariables(String variablesString) {
        Map<String, Double> variables = new HashMap<>();

        if (variablesString == null || variablesString.trim().isEmpty()) {
            return variables;
        }

        String[] assignments = variablesString.split(";");

        for (String assignment : assignments) {
            assignment = assignment.trim();

            if (assignment.isEmpty()) {
                continue;
            }

            String[] parts = assignment.split("=");

            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid variable assignment: " + assignment);
            }

            String varName = parts[0].trim();
            double varValue;

            try {
                varValue = Double.parseDouble(parts[1].trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format: " + parts[1]);
            }

            variables.put(varName, varValue);
        }

        return variables;
    }

    public Expression derivative(String variable) {
        if (this.name.equals(variable)) {
            return new Number(1);
        } else {
            return new Number(0);
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
