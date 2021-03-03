package common.network;

public class MethodNotFoundException extends RuntimeException {
    public MethodNotFoundException() {}

    public MethodNotFoundException(String message) {
        super(message);
    }

    public MethodNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
