package userservice.exceptions;

public class AddressNotFoundException extends RuntimeException{
    public AddressNotFoundException(){
        super("Address not found");
    }
    public AddressNotFoundException(String msg){
        super(msg);
    }
}
