package orderservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import orderservice.dto.CreateDeliveryAddressRequest;
import orderservice.dto.ErrorResponse;
import orderservice.dto.OrderDeliveryAddressResponse;
import orderservice.service.OrderDeliveryAddressService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/delivery-addresses")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Delivery Addresses", description = "APIs for managing order delivery addresses")
public class OrderDeliveryAddressController {

    private final OrderDeliveryAddressService deliveryAddressService;

    @GetMapping("/order/{orderId}")
    @Operation(
            summary = "Get delivery address by order ID",
            description = "Retrieves the delivery address for a specific order"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Delivery address retrieved successfully",
                    content = @Content(schema = @Schema(implementation = OrderDeliveryAddressResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid order ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order or delivery address not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<OrderDeliveryAddressResponse> getDeliveryAddressByOrderId(
            @Parameter(description = "Order ID", required = true)
            @PathVariable @Positive Long orderId) {
        log.info("Fetching delivery address for order: {}", orderId);
        OrderDeliveryAddressResponse response = deliveryAddressService.getDeliveryAddressByOrderId(orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/phone/{phone}")
    @Operation(
            summary = "Get delivery addresses by phone number",
            description = "Retrieves all delivery addresses associated with a phone number"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Delivery addresses retrieved successfully",
                    content = @Content(schema = @Schema(implementation = OrderDeliveryAddressResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid phone number",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<List<OrderDeliveryAddressResponse>> getDeliveryAddressesByPhone(
            @Parameter(description = "Phone number", required = true)
            @PathVariable @NotBlank String phone) {
        log.info("Fetching delivery addresses for phone: {}", phone);
        List<OrderDeliveryAddressResponse> response = deliveryAddressService.getDeliveryAddressesByPhone(phone);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pincode/{pincode}")
    @Operation(
            summary = "Get delivery addresses by pincode",
            description = "Retrieves all delivery addresses in a specific pincode area"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Delivery addresses retrieved successfully",
                    content = @Content(schema = @Schema(implementation = OrderDeliveryAddressResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid pincode",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<List<OrderDeliveryAddressResponse>> getDeliveryAddressesByPincode(
            @Parameter(description = "Pincode", required = true)
            @PathVariable @NotBlank String pincode) {
        log.info("Fetching delivery addresses for pincode: {}", pincode);
        List<OrderDeliveryAddressResponse> response = deliveryAddressService.getDeliveryAddressesByPincode(pincode);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/business/{businessId}/areas")
    @Operation(
            summary = "Get delivery areas by business",
            description = "Retrieves all unique delivery areas (localities/areas) where a business has delivered orders"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Delivery areas retrieved successfully",
                    content = @Content(schema = @Schema(type = "array", implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid business ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<List<String>> getDeliveryAreasByBusiness(
            @Parameter(description = "Business ID", required = true)
            @PathVariable @Positive Long businessId) {
        log.info("Fetching delivery areas for business: {}", businessId);
        List<String> deliveryAreas = deliveryAddressService.getDeliveryAreasByBusiness(businessId);
        return ResponseEntity.ok(deliveryAreas);
    }

    @PutMapping("/{addressId}")
    @Operation(
            summary = "Update delivery address",
            description = "Updates an existing delivery address with new information"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Delivery address updated successfully",
                    content = @Content(schema = @Schema(implementation = OrderDeliveryAddressResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid address ID or request data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Delivery address not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<OrderDeliveryAddressResponse> updateDeliveryAddress(
            @Parameter(description = "Delivery address ID", required = true)
            @PathVariable @Positive Long addressId,
            @Parameter(description = "Updated delivery address details", required = true)
            @Valid @RequestBody CreateDeliveryAddressRequest request) {
        log.info("Updating delivery address: {}", addressId);
        OrderDeliveryAddressResponse response = deliveryAddressService.updateDeliveryAddress(addressId, request);
        return ResponseEntity.ok(response);
    }
}
