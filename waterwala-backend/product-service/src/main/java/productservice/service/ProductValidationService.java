package productservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import productservice.client.BusinessServiceClient;
import productservice.client.UserServiceClient;
import productservice.client.BusinessValidationDto;
import productservice.client.UserValidationDto;
import productservice.exceptions.InvalidProductDataException;

/**
 * Service for validating business and user data via Feign clients
 * Centralizes all external service validation logic
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductValidationService {

    private final BusinessServiceClient businessServiceClient;
    private final UserServiceClient userServiceClient;

    /**
     * Validates that a business exists, is active, and verified
     *
     * @param businessId The business ID to validate (changed to Long)
     * @throws InvalidProductDataException if business is invalid
     */
    public void validateBusinessForProductCreation(Long businessId) {
        log.info("Validating business for product creation: {}", businessId);

        try {
            ResponseEntity<BusinessValidationDto> response =
                    businessServiceClient.validateBusiness(businessId);

            if (response.getBody() == null) {
                throw new InvalidProductDataException(
                        "Unable to validate business. Please try again."
                );
            }

            BusinessValidationDto validation = response.getBody();

            if (!Boolean.TRUE.equals(validation.getExists())) {
                throw new InvalidProductDataException(
                        "Business with ID " + businessId + " does not exist"
                );
            }

            if (!Boolean.TRUE.equals(validation.getIsActive())) {
                throw new InvalidProductDataException(
                        "Business is not active. Please contact support."
                );
            }

            if (!Boolean.TRUE.equals(validation.getIsVerified())) {
                throw new InvalidProductDataException(
                        "Business is not verified. Please complete verification before adding products."
                );
            }

            if (!Boolean.TRUE.equals(validation.getCanCreateProducts())) {
                throw new InvalidProductDataException(
                        "Business is not authorized to create products. Status: " +
                                validation.getVerificationStatus()
                );
            }

            log.info("Business validation successful for: {}", businessId);

        } catch (InvalidProductDataException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error validating business: {}", e.getMessage(), e);
            throw new InvalidProductDataException(
                    "Failed to validate business: " + e.getMessage()
            );
        }
    }

    /**
     * Validates that the authenticated user owns the specified business
     *
     * @param businessId The business ID (changed to Long)
     * @param userId The user ID (from JWT token)
     * @throws InvalidProductDataException if user doesn't own the business
     */
    public void validateBusinessOwnership(Long businessId, Long userId) {
        log.info("Validating business ownership: businessId={}, userId={}", businessId, userId);

        try {
            ResponseEntity<Boolean> response =
                    businessServiceClient.isBusinessOwner(businessId, userId);

            if (!Boolean.TRUE.equals(response.getBody())) {
                throw new InvalidProductDataException(
                        "You are not authorized to manage products for this business"
                );
            }

            log.info("Business ownership validated successfully");

        } catch (InvalidProductDataException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error validating business ownership: {}", e.getMessage(), e);
            throw new InvalidProductDataException(
                    "Failed to validate business ownership: " + e.getMessage()
            );
        }
    }

    /**
     * Validates that a user exists and is active
     *
     * @param userId The user ID to validate
     * @throws InvalidProductDataException if user is invalid
     */
    public void validateUser(Long userId) {
        log.info("Validating user: {}", userId);

        try {
            ResponseEntity<UserValidationDto> response =
                    userServiceClient.validateUser(userId);

            if (response.getBody() == null) {
                throw new InvalidProductDataException(
                        "Unable to validate user. Please try again."
                );
            }

            UserValidationDto validation = response.getBody();

            if (!Boolean.TRUE.equals(validation.getExists())) {
                throw new InvalidProductDataException(
                        "User does not exist"
                );
            }

            if (!Boolean.TRUE.equals(validation.getIsActive())) {
                throw new InvalidProductDataException(
                        "User account is not active"
                );
            }

            log.info("User validation successful for: {}", userId);

        } catch (InvalidProductDataException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error validating user: {}", e.getMessage(), e);
            throw new InvalidProductDataException(
                    "Failed to validate user: " + e.getMessage()
            );
        }
    }

    /**
     * Gets the current authenticated user ID from security context
     *
     * @return The user ID from JWT token
     * @throws InvalidProductDataException if no authentication found
     */
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new InvalidProductDataException("No authenticated user found");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof Long) {
            return (Long) principal;
        }

        try {
            return Long.valueOf(principal.toString());
        } catch (NumberFormatException e) {
            throw new InvalidProductDataException("Invalid user ID in authentication token");
        }
    }

    /**
     * Validates all requirements for product creation
     * - Business exists and is verified
     * - User owns the business (unless admin)
     *
     * @param businessId The business ID (changed to Long)
     */
    public void validateProductCreationRequirements(Long businessId) {
        log.info("Validating all product creation requirements for business: {}", businessId);

        // Validate business
        validateBusinessForProductCreation(businessId);

        // Get current user and validate ownership (skip for admins)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null &&
                !authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {

            Long userId = getCurrentUserId();
            validateBusinessOwnership(businessId, userId);
        }

        log.info("All product creation requirements validated successfully");
    }

    /**
     * Validates all requirements for product update
     * Same as creation, but may have different rules in the future
     *
     * @param businessId The business ID (changed to Long)
     */
    public void validateProductUpdateRequirements(Long businessId) {
        // For now, same as creation requirements
        validateProductCreationRequirements(businessId);
    }
}