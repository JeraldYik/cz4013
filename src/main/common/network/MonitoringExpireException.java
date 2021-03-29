package main.common.network;

public class MonitoringExpireException extends RuntimeException {
    public MonitoringExpireException() {
    }

    public MonitoringExpireException(String message) {
        super(message);
    }

    public MonitoringExpireException(String message, Throwable cause) {
        super(message, cause);
    }
}
