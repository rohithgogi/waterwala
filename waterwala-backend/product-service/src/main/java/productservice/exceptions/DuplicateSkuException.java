package productservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateSkuException extends RuntimeException {

    public DuplicateSkuException(String msg, Throwable cause){
        super(msg, cause);
    }
    public DuplicateSkuException(String sku){
        super("Product with sku: "+sku+" already exists");
    }
}
