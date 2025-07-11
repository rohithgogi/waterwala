package productservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotFoundException extends  RuntimeException{
    public ProductNotFoundException(String msg){
        super(msg);
    }

    public ProductNotFoundException(String msg,Throwable cause){
        super(msg, cause);
    }

    public ProductNotFoundException(Long productId){
        super("Product not found with ID: "+productId);
    }
}
