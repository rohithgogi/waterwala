package userservice.exceptions;

public class SessionNotFoundException extends RuntimeException{

    public SessionNotFoundException(){
        super("Session not found");
    }

    public SessionNotFoundException(String msg){
        super(msg);
    }
}
