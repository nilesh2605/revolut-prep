package loadbalancer.exception;

public class NoInstancesAvailableException extends RuntimeException {
    public NoInstancesAvailableException(String message) {
        super(message);
    }
}