package paymentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import paymentservice.dto.OrderDetailsResponse;
import paymentservice.dto.OrderStatusUpdateRequest;

@FeignClient(name = "order-service")
public interface OrderServiceClient {

    @GetMapping("/api/v1/orders/{orderId}")
    OrderDetailsResponse getOrderById(@PathVariable("orderId") Long orderId);

    @PatchMapping("/api/v1/orders/{orderId}/status")
    void updateOrderStatus(
            @PathVariable("orderId") Long orderId,
            @RequestBody OrderStatusUpdateRequest request
    );
}
