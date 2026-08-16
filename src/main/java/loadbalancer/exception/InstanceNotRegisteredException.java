package loadbalancer.exception;

public class InstanceNotRegisteredException extends RuntimeException{
    public InstanceNotRegisteredException(String message) {
        super(message);
    }
}
