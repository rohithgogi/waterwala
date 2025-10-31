package businessservice.exceptions;

public class BusinessOperationException extends RuntimeException{
    public BusinessOperationException(String msg){
        super(msg);
    }
}
