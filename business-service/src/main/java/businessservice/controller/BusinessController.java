package businessservice.controller;

import businessservice.dto.*;
import businessservice.model.ServiceType;
import businessservice.service.BusinessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.SortDirection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/businesses")
@RequiredArgsConstructor
@Tag(name = "Business Management", description = "APIs for managing business operations")
public class BusinessController {
    private final BusinessService businessService;


    @PostMapping("/register")
    @Operation(summary = "Register a new business", description = "Register a new business with all required information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Business registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid business data"),
            @ApiResponse(responseCode = "409", description = "Business already exists")
    })
    public ResponseEntity<BusinessProfileDto> registerBusiness(@Valid @RequestBody BusinessRegistrationDto registrationDto){
        BusinessProfileDto business=businessService.registerBusiness(registrationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(business);
    }

    @GetMapping("/{businessId}")
    @Operation(summary = "Get business profile", description = "Get business profile by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Business found"),
            @ApiResponse(responseCode = "404", description = "Business not found")
    })
    public ResponseEntity<BusinessProfileDto> getBusinessProfile(
            @Parameter(description = "Business ID") @PathVariable Long businessId) {
        BusinessProfileDto business = businessService.getBusinessProfile(businessId);
        return ResponseEntity.ok(business);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get business by user ID", description = "Get business profile by user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Business found"),
            @ApiResponse(responseCode = "404", description = "Business not found")
    })
    public ResponseEntity<BusinessProfileDto> getBusinessByUserId(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        BusinessProfileDto business = businessService.getBusinessByUserId(userId);
        return ResponseEntity.ok(business);
    }

    @PutMapping("/{businessId}")
    @Operation(summary = "Update business profile", description = "U[date business profile information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Business updated successfully"),
            @ApiResponse(responseCode = "403", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Business not found")
    })
    @PreAuthorize("hasRole('Business_OWNER')")
    public ResponseEntity<BusinessProfileDto> updateBusinessProfile(
            @Parameter(description = "Business ID") @PathVariable Long businessId,
            @Valid @RequestBody BusinessUpdateDto updateDto,
            @Parameter(description = "User ID") @RequestHeader("X-User-Id") Long userId) {
        BusinessProfileDto business = businessService.updateBusinessProfile(businessId, updateDto, userId);
        return ResponseEntity.ok(business);
    }

    @PostMapping("/{businessId}/verify")
    @Operation(summary = "Verify business", description = "Verify business by Admin")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "200", description = "Business Verification updated"),
            @ApiResponse(responseCode = "404", description = "Business not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> verifyBusiness(
            @Parameter(description = "Business Id") @PathVariable Long businessId,
            @Valid @RequestBody BusinessVerificationDto verificationDto
    ){
        businessService.verifyBusiness(businessId,verificationDto.getStatus(),verificationDto.getComments());
        return ResponseEntity.ok().build();

    }

    @GetMapping("/search")
    @Operation(summary = "Search businesses", description = "Search businesses by various criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search completed successfully")
    })
    public ResponseEntity<List<BusinessProfileDto>> searchBusinesses(
            @Parameter(description = "Pincode") @RequestParam(required = false) String pincode,
            @Parameter(description = "City") @RequestParam(required = false) String city,
            @Parameter(description = "State") @RequestParam(required = false) String state,
            @Parameter(description = "Service type") @RequestParam(required = false) String serviceType,
            @Parameter(description = "Keyword") @RequestParam(required = false) String keyword,
            @Parameter(description = "Latitude") @RequestParam(required = false) Double latitude,
            @Parameter(description = "Longitude") @RequestParam(required = false) Double longitude,
            @Parameter(description = "Radius in km") @RequestParam(required = false) Integer radius,
            @Parameter(description = "Minimum rating") @RequestParam(required = false) Double minRating,
            @Parameter(description = "Business type") @RequestParam(required = false) String businessType) {

        BusinessSearchDto searchDto = BusinessSearchDto.builder()
                .pincode(pincode)
                .city(city)
                .state(state)
                .serviceType(serviceType != null ? ServiceType.valueOf(serviceType) : null)
                .keyword(keyword)
                .latitude(latitude)
                .longitude(longitude)
                .radius(radius)
                .minRating(minRating)
                .businessType(businessType)
                .build();

        List<BusinessProfileDto> businesses = businessService.searchBusinesses(searchDto);
        return ResponseEntity.ok(businesses);
    }

    @GetMapping("/featured")
    @Operation(summary = "Get featured businesses", description = "Get featured businesses with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Featured businesses retrieved successfully")
    })
    public ResponseEntity<Page<BusinessProfileDto>> getFeaturedBusinesses(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by") @RequestParam(defaultValue = "averageRating") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir){
        Sort sort=Sort.by(Sort.Direction.fromString(sortDir),sortBy);
        Pageable pageable= PageRequest.of(page,size,sort);

        Page<BusinessProfileDto> businesses = businessService.getFeaturedBusinesses(pageable);
        return ResponseEntity.ok(businesses);
    }

    @PatchMapping("/{businessId}/rating")
    @Operation(summary = "Update business rating", description = "Update business rating and review count")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rating updated successfully"),
            @ApiResponse(responseCode = "404", description = "Business not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateBusinessRating(
            @Parameter(description = "Business ID") @PathVariable Long businessId,
            @Parameter(description = "Average rating") @RequestParam Double rating,
            @Parameter(description = "Review count") @RequestParam Integer reviewCount) {
        businessService.updateBusinessRating(businessId, rating, reviewCount);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{businessId}/orders")
    @Operation(summary = "Update order statistics", description = "Update business order statistics")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order stats updated successfully"),
            @ApiResponse(responseCode = "404", description = "Business not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateOrderStats(
            @Parameter(description = "Business ID") @PathVariable Long businessId,
            @Parameter(description = "Total orders") @RequestParam Integer totalOrders,
            @Parameter(description = "Completed orders") @RequestParam Integer completedOrders) {
        businessService.updateOrderStats(businessId, totalOrders, completedOrders);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{businessId}")
    @Operation(summary = "Deactivate business", description = "Deactivate business account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Business deactivated successfully"),
            @ApiResponse(responseCode = "403", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Business not found")
    })
    @PreAuthorize("hasRole('BUSINESS_OWNER')")
    public ResponseEntity<Void> deactivateBusiness(
            @Parameter(description = "Business ID") @PathVariable Long businessId,
            @Parameter(description = "User ID") @RequestHeader("X-User-Id") Long userId) {
        businessService.deactivateBusiness(businessId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{businessId}/owner")
    @Operation(summary = "Check business ownership", description = "Check if user is the business owner")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ownership check completed")
    })
    public ResponseEntity<Boolean> isBusinessOwner(
            @Parameter(description = "Business ID") @PathVariable Long businessId,
            @Parameter(description = "User ID") @RequestHeader("X-User-Id") Long userId) {
        boolean isOwner = businessService.isBusinessOwner(businessId, userId);
        return ResponseEntity.ok(isOwner);
    }
}
