package lb.exception;

public class DuplicateAddressLbException extends RuntimeException{
    public DuplicateAddressLbException(String message) {
        super(message);
    }
}
