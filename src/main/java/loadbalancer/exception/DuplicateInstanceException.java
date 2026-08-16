package loadbalancer.exception;

public class DuplicateInstanceException extends RuntimeException {
    public DuplicateInstanceException(String message) {
        super(message);
    }
}
