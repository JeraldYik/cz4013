package main.common.network;

/**
 * The type Method not found exception.
 */
public class MethodNotFoundException extends RuntimeException {
    /**
     * Instantiates a new Method not found exception.
     */
    public MethodNotFoundException() {}

    /**
     * Instantiates a new Method not found exception.
     *
     * @param message the message
     */
    public MethodNotFoundException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Method not found exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public MethodNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
