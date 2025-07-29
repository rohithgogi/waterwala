package orderservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import orderservice.dto.ErrorResponse;
import orderservice.dto.OrderCreateRequest;
import orderservice.dto.OrderResponse;
import orderservice.model.OrderStatus;
import orderservice.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Order Management", description = "API for managing orders")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Creating new order", description = "Create a new order with items and delivery address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody @Parameter(description = "Order creation request") OrderCreateRequest request){
        log.info("Creating order for customer: {}",request.getCustomerId());
        OrderResponse response=orderService.createOrder(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by Id", description = "Retrieves an order of it unique identification number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order found successfully",
            content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "404", description = "Order not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid order ID",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)
            )
    )
    })
    public ResponseEntity<OrderResponse> getOrderById(@Parameter(description = "Order ID", required = true) @PathVariable @Positive Long id){
        log.info("Fetching order with Id: {}",id);
        OrderResponse response=orderService.getOrderById(id);
        log.info("Successfully retrieved order: {}", response.getOrderNumber());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/number/{orderNumber}")
    @Operation(summary = "Get order by ordernumber", description = "Retrives an order by its unique order number")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Order found successfully",
                    content = @Content(
                            schema = @Schema(implementation = OrderResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<OrderResponse> getOrderByNumber(
            @Parameter(description = "Order number", required = true)
            @PathVariable @NotNull String orderNumber) {
        log.info("Fetching order with number: {}", orderNumber);
        OrderResponse response = orderService.getOrderByNumber(orderNumber);
        log.info("Successfully retrieved order with number: {}", orderNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/{customerId}")
    @Operation(
            summary = "Get orders by customer ID",
            description = "Retrieves all orders for a specific customer"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Orders retrieved successfully",
                    content = @Content(
                            schema = @Schema(implementation = OrderResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid customer ID",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<List<OrderResponse>> getOrdersByCustomer(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable @Positive Long customerId) {
        log.info("Fetching orders for customer: {}", customerId);
        List<OrderResponse> response = orderService.getOrdersByCustomer(customerId);
        log.info("Retrieved {} orders for customer: {}", response.size(), customerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/{customerId}/paginated")
    @Operation(
            summary = "Get paginated orders by customer ID",
            description = "Retrieves orders for a specific customer with pagination support"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Paginated orders retrieved successfully",
                    content = @Content(
                            schema = @Schema(implementation = Page.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid customer ID or pagination parameters",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<Page<OrderResponse>> getOrdersByCustomerPaginated(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable @Positive Long customerId,
            @Parameter(description = "Pagination parameters (page, size, sort)")
            Pageable pageable) {
        log.info("Fetching paginated orders for customer: {} with page: {}", customerId, pageable.getPageNumber());
        Page<OrderResponse> response = orderService.getOrdersByCustomer(customerId, pageable);
        log.info("Retrieved {} orders for customer: {} (page {} of {})",
                response.getNumberOfElements(), customerId,
                response.getNumber(), response.getTotalPages());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/business/{businessId}")
    @Operation(
            summary = "Get orders by business ID",
            description = "Retrieves all orders for a specific business"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Orders retrieved successfully",
                    content = @Content(
                            schema = @Schema(implementation = OrderResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid business ID",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<List<OrderResponse>> getOrdersByBusiness(
            @Parameter(description = "Business ID", required = true)
            @PathVariable @Positive Long businessId) {
        log.info("Fetching orders for business: {}", businessId);
        List<OrderResponse> response = orderService.getOrdersByBusiness(businessId);
        log.info("Retrieved {} orders for business: {}", response.size(), businessId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/business/{businessId}/paginated")
    @Operation(
            summary = "Get paginated orders by business ID",
            description = "Retrieves orders for a specific business with pagination support"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Paginated orders retrieved successfully",
                    content = @Content(
                            schema = @Schema(implementation = Page.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid business ID or pagination parameters",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<Page<OrderResponse>> getOrdersByBusinessPaginated(
            @Parameter(description = "Business ID", required = true)
            @PathVariable @Positive Long businessId,
            @Parameter(description = "Pagination parameters (page, size, sort)")
            Pageable pageable) {
        log.info("Fetching paginated orders for business: {} with page: {}", businessId, pageable.getPageNumber());
        Page<OrderResponse> response = orderService.getOrdersByBusiness(businessId, pageable);
        log.info("Retrieved {} orders for business: {} (page {} of {})",
                response.getNumberOfElements(), businessId,
                response.getNumber(), response.getTotalPages());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/confirm")
    @Operation(
            summary = "Confirm an order",
            description = "Changes the order status to confirmed. Only pending orders can be confirmed."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Order confirmed successfully",
                    content = @Content(
                            schema = @Schema(implementation = OrderResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid order status for confirmation",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<OrderResponse> confirmOrder(
            @Parameter(description = "Order ID", required = true)
            @PathVariable @Positive Long id) {
        log.info("Attempting to confirm order: {}", id);
        OrderResponse response = orderService.confirmOrder(id);
        log.info("Successfully confirmed order: {}", response.getOrderNumber());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/dispatch")
    @Operation(
            summary = "Dispatch an order",
            description = "Assigns a delivery person to the order and changes status to dispatched. Only confirmed orders can be dispatched."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Order dispatched successfully",
                    content = @Content(
                            schema = @Schema(implementation = OrderResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid order status for dispatch or invalid delivery person ID",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<OrderResponse> dispatchOrder(
            @Parameter(description = "Order ID", required = true)
            @PathVariable @Positive Long id,
            @Parameter(description = "Delivery person ID", required = true, example = "5")
            @RequestParam @Positive Long deliveryPersonId) {
        log.info("Attempting to dispatch order: {} to delivery person: {}", id, deliveryPersonId);
        OrderResponse response = orderService.dispatchOrder(id, deliveryPersonId);
        log.info("Successfully dispatched order: {} to delivery person: {}",
                response.getOrderNumber(), deliveryPersonId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/deliver")
    @Operation(
            summary = "Mark order as delivered",
            description = "Changes the order status to delivered. Only dispatched orders can be marked as delivered."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Order marked as delivered successfully",
                    content = @Content(
                            schema = @Schema(implementation = OrderResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid order status for delivery",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<OrderResponse> deliverOrder(
            @Parameter(description = "Order ID", required = true)
            @PathVariable @Positive Long id) {
        log.info("Attempting to mark order as delivered: {}", id);
        OrderResponse response = orderService.deliverOrder(id);
        log.info("Successfully marked order as delivered: {}", response.getOrderNumber());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/cancel")
    @Operation(
            summary = "Cancel an order",
            description = "Cancels an order with a specified reason. Orders can be cancelled if they are not already delivered."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Order cancelled successfully",
                    content = @Content(
                            schema = @Schema(implementation = OrderResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid order status for cancellation or missing cancellation reason",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<OrderResponse> cancelOrder(
            @Parameter(description = "Order ID", required = true)
            @PathVariable @Positive Long id,
            @Parameter(description = "Reason for cancellation", required = true, example = "Customer request")
            @RequestParam @NotNull String reason) {
        log.info("Attempting to cancel order: {} with reason: {}", id, reason);
        OrderResponse response = orderService.cancelOrder(id, reason);
        log.info("Successfully cancelled order: {} with reason: {}", response.getOrderNumber(), reason);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @Operation(
            summary = "Get orders by status",
            description = "Retrieves all orders with a specific status"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Orders retrieved successfully",
                    content = @Content(
                            schema = @Schema(implementation = OrderResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid order status",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(
            @Parameter(description = "Order status", required = true)
            @PathVariable OrderStatus status) {
        log.info("Fetching orders with status: {}", status);
        List<OrderResponse> response = orderService.getOrdersByStatus(status);
        log.info("Retrieved {} orders with status: {}", response.size(), status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/analytics/business/{businessId}/count")
    @Operation(
            summary = "Get business order count by status",
            description = "Returns the count of orders for a specific business filtered by order status"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Order count retrieved successfully",
                    content = @Content(
                            schema = @Schema(implementation = Long.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid business ID or order status",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<Long> getBusinessOrderCount(
            @Parameter(description = "Business ID", required = true)
            @PathVariable @Positive Long businessId,
            @Parameter(description = "Order status to filter by", required = true, example = "DELIVERED")
            @RequestParam OrderStatus status) {
        log.info("Fetching order count for business: {} with status: {}", businessId, status);
        Long count = orderService.getBusinessOrderCount(businessId, status);
        log.info("Business {} has {} orders with status: {}", businessId, count, status);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/analytics/business/{businessId}/revenue")
    @Operation(
            summary = "Get business total revenue",
            description = "Returns the total revenue generated by a specific business from all completed orders"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Revenue retrieved successfully",
                    content = @Content(
                            schema = @Schema(implementation = java.math.BigDecimal.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid business ID",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<java.math.BigDecimal> getBusinessRevenue(
            @Parameter(description = "Business ID", required = true)
            @PathVariable @Positive Long businessId) {
        log.info("Fetching revenue for business: {}", businessId);
        java.math.BigDecimal revenue = orderService.getBusinessRevenue(businessId);
        log.info("Business {} has total revenue: {}", businessId, revenue);
        return ResponseEntity.ok(revenue);
    }
}
