package orderservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import orderservice.dto.ErrorResponse;
import orderservice.dto.OrderItemResponse;
import orderservice.model.OrderItem;
import orderservice.model.OrderItemStatus;
import orderservice.service.OrderItemService;
import orderservice.utility.OrderMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order-items")
@Slf4j
@Validated
@Tag(name = "Order items management", description = "APIs for managing individual order items")
public class OrderItemController {
    private final OrderItemService orderItemService;
    private final OrderMapper orderMapper;

    @GetMapping("/order/{orderId}")
    @Operation(
            summary = "Get order items by order ID",
            description = "Retrieves all items for a specific order"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Order items retrieved successfully",
                    content = @Content(schema = @Schema(implementation = OrderItemResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid order ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<List<OrderItemResponse>> getOrderItems(
            @Parameter(description = "Order ID", required = true)
            @PathVariable @Positive Long orderId) {
        log.info("Fetching items for order: {}", orderId);
        List<OrderItem> items = orderItemService.getOrderItems(orderId);
        List<OrderItemResponse> response = orderMapper.toItemResponseList(items);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{itemId}/status")
    @Operation(
            summary = "Update order item status",
            description = "Updates the status of a specific order item"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Item status updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid item ID or status",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order item not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<Void> updateItemStatus(
            @Parameter(description = "Order item ID", required = true)
            @PathVariable @Positive Long itemId,
            @Parameter(description = "New status for the order item", required = true, example = "DELIVERED")
            @RequestParam @NotNull OrderItemStatus status) {
        log.info("Updating item {} status to: {}", itemId, status);
        orderItemService.updateItemStatus(itemId, status);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/order/{orderId}/deliver-all")
    @Operation(
            summary = "Mark all order items as delivered",
            description = "Marks all items in an order as delivered"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "All items marked as delivered successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid order ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<Void> markAllItemsAsDelivered(
            @Parameter(description = "Order ID", required = true)
            @PathVariable @Positive Long orderId) {
        log.info("Marking all items as delivered for order: {}", orderId);
        orderItemService.markItemsAsDelivered(orderId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/product/{productId}/total-sold")
    @Operation(
            summary = "Get total quantity sold for a product",
            description = "Returns the total quantity sold across all orders for a specific product"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Total quantity retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Integer.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid product ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<Integer> getTotalQuantitySold(
            @Parameter(description = "Product ID", required = true)
            @PathVariable @Positive Long productId) {
        log.info("Fetching total quantity sold for product: {}", productId);
        Integer totalSold = orderItemService.getTotalQuantitySold(productId);
        return ResponseEntity.ok(totalSold);
    }

    @GetMapping("/status/{status}")
    @Operation(
            summary = "Get order items by status",
            description = "Retrieves all order items with a specific status"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Order items retrieved successfully",
                    content = @Content(schema = @Schema(implementation = OrderItemResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid status",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<List<OrderItemResponse>> getItemsByStatus(
            @Parameter(description = "Order item status", required = true)
            @PathVariable OrderItemStatus status) {
        log.info("Fetching items with status: {}", status);
        // This would require adding a method to the service
        // For now, returning empty list as placeholder
        return ResponseEntity.ok(List.of());
    }

}
