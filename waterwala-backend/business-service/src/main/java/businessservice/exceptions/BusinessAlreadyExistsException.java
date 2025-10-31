package businessservice.exceptions;

public class BusinessAlreadyExistsException extends RuntimeException{
    public BusinessAlreadyExistsException(String msg){
        super(msg);
    }
}
