package productservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import productservice.configuration.FeignConfig;

/**
 * Feign client for communicating with Business Service
 *
 * Used to validate business existence and ownership before product operations
 *
 * Configuration includes:
 * - Custom error decoder for better error handling
 * - Timeouts: 5s connect, 10s read
 * - Retry mechanism: 3 attempts with 1-2s intervals
 * - JWT token propagation
 */
@FeignClient(
        name = "business-service",url = "http://localhost:8082",
        configuration = FeignConfig.class
)
public interface BusinessServiceClient {

    /**
     * Validates business existence, verification status, and operational state
     * Used during product creation/update to verify business credentials
     *
     * @param businessId The ID of the business to validate
     * @return BusinessValidationDto containing business details and validation status
     */
    @GetMapping("/api/v1/businesses/validate/{businessId}")
    ResponseEntity<BusinessValidationDto> validateBusiness(@PathVariable("businessId") Long businessId);

    /**
     * Verifies if a user is the owner of a specific business
     * Used for authorization checks during product operations
     *
     * @param businessId The ID of the business
     * @param userId The ID of the user to check ownership for
     * @return Boolean indicating if the user owns the business
     */
    @GetMapping("/api/v1/businesses/{businessId}/owner/{userId}")
    ResponseEntity<Boolean> isBusinessOwner(
            @PathVariable("businessId") Long businessId,
            @PathVariable("userId") Long userId
    );

    /**
     * Gets basic business information
     * Used to enrich product responses with business details if needed
     *
     * @param businessId The ID of the business
     * @return Basic business information
     */
    @GetMapping("/api/v1/businesses/{businessId}/info")
    ResponseEntity<BusinessValidationDto> getBusinessInfo(@PathVariable("businessId") Long businessId);
}