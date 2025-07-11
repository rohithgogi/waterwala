package productservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InsufficientStockException extends RuntimeException{
    public InsufficientStockException(String message) {
        super(message);
    }

    public InsufficientStockException(String message, Throwable cause) {
        super(message, cause);
    }

    public InsufficientStockException(Long productId, int requestedQuantity, int availableQuantity) {
        super(String.format("Insufficient stock for product ID: %d. Requested: %d, Available: %d",
                productId, requestedQuantity, availableQuantity));
    }
}
