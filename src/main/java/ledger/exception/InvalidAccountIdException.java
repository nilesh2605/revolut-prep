package ledger.exception;

public class InvalidAccountIdException extends RuntimeException{
    public InvalidAccountIdException(String messsage){
        super(messsage);
    }
}
