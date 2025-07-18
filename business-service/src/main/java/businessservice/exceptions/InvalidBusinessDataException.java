package businessservice.exceptions;

public class InvalidBusinessDataException extends RuntimeException{

    public InvalidBusinessDataException(String msg){
        super(msg);
    }
}
