package orderservice.exceptions;

public class InvalidOrderStatusException extends RuntimeException {
    private final String errorCode;

    public InvalidOrderStatusException(String message) {
        super(message);
        this.errorCode = "INVALID_ORDER_STATUS";
    }

    public InvalidOrderStatusException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}