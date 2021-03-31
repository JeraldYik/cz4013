package main.common.network;

/**
 * The type Monitoring expire exception.
 */
public class MonitoringExpireException extends RuntimeException {
    /**
     * Instantiates a new Monitoring expire exception.
     */
    public MonitoringExpireException() {
    }

    /**
     * Instantiates a new Monitoring expire exception.
     *
     * @param message the message
     */
    public MonitoringExpireException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Monitoring expire exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public MonitoringExpireException(String message, Throwable cause) {
        super(message, cause);
    }
}
