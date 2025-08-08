package userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import userservice.dto.AddressDto;
import userservice.dto.AddressResponseDto;
import userservice.dto.CommonResponseDto.StandardResponse;
import userservice.service.AddressService;

import java.util.List;

@RestController
@RequestMapping("api/v1/addresses")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Address Management", description = "APIs for managing user addresses")
@SecurityRequirement(name = "bearerAuth")
public class AddressController {
    private final AddressService addressService;

    @PostMapping("/user/{userId}")
    @Operation(
            summary = "Add new address for user",
            description = "Creates a new address entry for the specified user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Address created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("@securityService.canModifyAddresses(#userId)")
    public ResponseEntity<StandardResponse<AddressResponseDto>> addAddress(@Parameter(description = "User ID") @PathVariable Long userId, @Valid @RequestBody AddressDto addressDto){
        AddressResponseDto address= addressService.addAddress(userId,addressDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StandardResponse.success("Address added successfully",address));
    }

    @PutMapping("/{addressId}")
    @Operation(
            summary = "Update existing address",
            description = "Updates an existing address with new information"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address updated successfully"),
            @ApiResponse(responseCode = "404", description = "Address not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PreAuthorize("@securityService.ownsAddress(#addressId)")
    public ResponseEntity<StandardResponse<AddressResponseDto>> updateAddress(@Parameter(description = "Address ID") @PathVariable Long addressId, @Valid @RequestBody AddressDto addressDto){
        AddressResponseDto address=addressService.updateAddress(addressId,addressDto);
        return ResponseEntity.ok(StandardResponse.success("Address updated successfully",address));
    }

    @DeleteMapping("/{addressId}")
    @Operation(
            summary = "Delete address",
            description = "Permanently deletes the specified address"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Address deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Address not found")
    })
    @PreAuthorize("@securityService.ownsAddress(#addreddId)")
    public ResponseEntity<StandardResponse<String>> deleteAddress( @Parameter(description = "Address ID") @PathVariable Long addressId){
        addressService.deleteAddress(addressId);
        return ResponseEntity.ok(StandardResponse.success("Address deleted successfully"));
    }

    @PatchMapping("/{addressId}/set-default")
    @Operation(
            summary = "Set default address",
            description = "Sets the specified address as the default address for the user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Default address updated successfully"),
            @ApiResponse(responseCode = "404", description = "Address not found")
    })
    @PreAuthorize("@securityService.ownsAddress(#AddressId)")
    public ResponseEntity<StandardResponse<AddressResponseDto>> setDefaultAddress(@Parameter(description = "Address ID")@PathVariable Long addressId){
        addressService.setDefaultAddress(addressId);
        return ResponseEntity.ok(StandardResponse.success("Default address updated successfully"));
    }

    @GetMapping("user/{userId}")
    @Operation(
            summary = "Get all addresses for user",
            description = "Retrieves all addresses associated with the specified user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Addresses retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("@securityService.canAccessAddresses(#userId)")
    public ResponseEntity<StandardResponse<List<AddressResponseDto>>> getAllAddressesOfUser(@Parameter(description = "User ID") @PathVariable Long userId){
        List<AddressResponseDto> addresses=addressService.getAllAddresses(userId);
        return ResponseEntity.ok(StandardResponse.success("Addresses retrieved successfully",addresses));
    }

    @GetMapping("user/{userId}/default")
    @Operation(
            summary = "Get default address for user",
            description = "Retrieves the default address for the specified user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Default addresses retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Address not found")
    })
    @PreAuthorize("@securityService.canAccessAddresses(#userId)")
    public ResponseEntity<StandardResponse<AddressResponseDto>> getDefaultAddress(@Parameter(description = "User ID") @PathVariable Long userId){
        AddressResponseDto address=addressService.getDefaultAddress(userId);
        return ResponseEntity.ok(StandardResponse.success("Default address retrieved successfully",address));
    }

    @GetMapping("/{addressId}")
    @Operation(
            summary = "Get address by ID",
            description = "Retrieves a specific address by its ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Addresses retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Address not found")
    })
    @PreAuthorize("@securityService.ownsAddress(#addressId)")
    public ResponseEntity<StandardResponse<AddressResponseDto>> getAddressById(@PathVariable Long addressId){
        AddressResponseDto address= addressService.getAddressById(addressId);
        return ResponseEntity.ok(StandardResponse.success("Address retrieved successfully",address));
    }

}
