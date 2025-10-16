package productservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import productservice.client.UserValidationDto;
import productservice.configuration.FeignConfig;

/**
 * Feign client for communicating with User Service
 *
 * Used to validate user existence, status, and role
 *
 * Configuration includes:
 * - Custom error decoder for better error handling
 * - Timeouts: 5s connect, 10s read
 * - Retry mechanism: 3 attempts with 1-2s intervals
 * - JWT token propagation
 */
@FeignClient(
        name = "user-service",
        configuration = FeignConfig.class
)
public interface UserServiceClient {

    /**
     * Validates user existence, status, and role
     * Used during product operations to verify user credentials
     *
     * @param userId The ID of the user to validate
     * @return UserValidationDto containing user details and validation status
     */
    @GetMapping("/api/v1/users/{userId}/validate")
    ResponseEntity<UserValidationDto> validateUser(@PathVariable("userId") Long userId);
}