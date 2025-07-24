package orderservice.dto;

import lombok.Builder;
import lombok.Data;
import orderservice.model.DeliveryType;
import orderservice.model.OrderStatus;
import orderservice.model.OrderType;
import orderservice.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private Long customerId;
    private Long businessId;
    private OrderType orderType;
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private DeliveryType deliveryType;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal deliveryCharges;
    private BigDecimal totalAmount;
    private String specialInstructions;
    private LocalDateTime scheduledAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime dispatchedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime cancelledAt;
    private String cancellationReason;
    private Long assignedDeliveryPersonId;
    private List<OrderItemResponse> orderItems;
    private OrderDeliveryAddressResponse deliveryAddress;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
