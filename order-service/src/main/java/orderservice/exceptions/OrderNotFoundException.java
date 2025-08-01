package orderservice.exceptions;

public class OrderNotFoundException extends RuntimeException {
    private final String errorCode;

    public OrderNotFoundException(String message) {
        super(message);
        this.errorCode = "ORDER_NOT_FOUND";
    }

    public OrderNotFoundException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}