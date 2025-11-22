package expressions.exceptions;

public class UnknownVariableException extends RuntimeException {
    public UnknownVariableException(String message) {
        super(message);
    }
}
