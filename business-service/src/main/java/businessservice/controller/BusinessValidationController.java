package businessservice.controller;

import businessservice.dto.BusinessValidationDto;
import businessservice.model.Business;
import businessservice.model.VerificationStatus;
import businessservice.repository.BusinessRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for business validation endpoints
 * Used by other microservices to validate business status
 */
@RestController
@RequestMapping("/api/v1/businesses")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Business Validation", description = "APIs for validating business status for inter-service communication")
@Slf4j
public class BusinessValidationController {

    private final BusinessRepository businessRepository;

    @GetMapping("/{businessId}/validate")
    @Operation(
            summary = "Validate business for product creation",
            description = "Validates if a business exists, is active, verified, and can create products"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Validation completed"),
            @ApiResponse(responseCode = "404", description = "Business not found")
    })
    public ResponseEntity<BusinessValidationDto> validateBusiness(
            @Parameter(description = "Business ID") @PathVariable Long businessId) {

        log.info("Validating business with ID: {}", businessId);

        return businessRepository.findById(businessId)
                .map(business -> {
                    boolean canCreateProducts = business.getIsActive() &&
                            (business.getVerificationStatus() == VerificationStatus.APPROVED);

                    BusinessValidationDto validation = BusinessValidationDto.builder()
                            .exists(true)
                            .isActive(business.getIsActive())
                            .isVerified(business.getVerificationStatus() == VerificationStatus.APPROVED)
                            .businessId(business.getId())
                            .businessName(business.getBusinessName())
                            .ownerId(business.getUserId())
                            .licenseNumber(business.getBusinessRegistrationNumber())
                            .verificationStatus(business.getVerificationStatus().toString())
                            .canCreateProducts(canCreateProducts)
                            .businessType(business.getBusinessType())
                            .message(canCreateProducts ? "Business is valid for product creation" :
                                    "Business cannot create products - Status: " + business.getVerificationStatus())
                            .build();

                    log.info("Business validation successful for ID: {}, canCreateProducts: {}",
                            businessId, canCreateProducts);
                    return ResponseEntity.ok(validation);
                })
                .orElseGet(() -> {
                    log.warn("Business not found with ID: {}", businessId);
                    BusinessValidationDto validation = BusinessValidationDto.builder()
                            .exists(false)
                            .isActive(false)
                            .isVerified(false)
                            .businessId(businessId)
                            .canCreateProducts(false)
                            .message("Business not found")
                            .build();
                    return ResponseEntity.ok(validation);
                });
    }

    @GetMapping("/{businessId}/info")
    @Operation(
            summary = "Get basic business information",
            description = "Returns basic business information for display purposes"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Business info retrieved"),
            @ApiResponse(responseCode = "404", description = "Business not found")
    })
    public ResponseEntity<BusinessValidationDto> getBusinessInfo(
            @Parameter(description = "Business ID") @PathVariable Long businessId) {

        log.debug("Getting business info for ID: {}", businessId);

        return businessRepository.findById(businessId)
                .map(business -> {
                    BusinessValidationDto info = BusinessValidationDto.builder()
                            .exists(true)
                            .businessId(business.getId())
                            .businessName(business.getBusinessName())
                            .businessType(business.getBusinessType())
                            .isActive(business.getIsActive())
                            .isVerified(business.getVerificationStatus() == VerificationStatus.APPROVED)
                            .verificationStatus(business.getVerificationStatus().toString())
                            .ownerId(business.getUserId())
                            .build();

                    return ResponseEntity.ok(info);
                })
                .orElseGet(() -> {
                    log.warn("Business not found with ID: {}", businessId);
                    BusinessValidationDto info = BusinessValidationDto.builder()
                            .exists(false)
                            .businessId(businessId)
                            .message("Business not found")
                            .build();
                    return ResponseEntity.ok(info);
                });
    }

    @GetMapping("/{businessId}/owner/{userId}")
    @Operation(
            summary = "Check if user owns business",
            description = "Validates if a specific user is the owner of a business"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ownership check completed")
    })
    public ResponseEntity<Boolean> isBusinessOwner(
            @Parameter(description = "Business ID") @PathVariable Long businessId,
            @Parameter(description = "User ID") @PathVariable Long userId) {

        log.debug("Checking if user {} owns business {}", userId, businessId);

        boolean isOwner = businessRepository.findById(businessId)
                .map(business -> business.getUserId().equals(userId))
                .orElse(false);

        log.debug("Ownership check result: {}", isOwner);
        return ResponseEntity.ok(isOwner);
    }
}