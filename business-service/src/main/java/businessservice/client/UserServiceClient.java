package businessservice.client;

import businessservice.client.dto.UserValidationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserServiceClient {
    @GetMapping("/api/v1/users/{userId}/validate")
    ResponseEntity<UserValidationResponse> validateUser(@PathVariable("userId") Long userId);


}
