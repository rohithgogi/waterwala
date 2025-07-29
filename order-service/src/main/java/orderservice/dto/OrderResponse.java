package orderservice.dto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import orderservice.model.DeliveryType;
import orderservice.model.OrderStatus;
import orderservice.model.OrderType;
import orderservice.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
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

    // Sensitive field - only include for authorized users
    private Long assignedDeliveryPersonId;

    private List<OrderItemResponse> orderItems;
    private OrderDeliveryAddressResponse deliveryAddress;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Customer-specific response (hide sensitive business data)
    public static OrderResponse createCustomerResponse(OrderResponse full) {
        return OrderResponse.builder()
                .id(full.getId())
                .orderNumber(full.getOrderNumber())
                .customerId(full.getCustomerId())
                .orderType(full.getOrderType())
                .status(full.getStatus())
                .paymentStatus(full.getPaymentStatus())
                .deliveryType(full.getDeliveryType())
                .subtotal(full.getSubtotal())
                .taxAmount(full.getTaxAmount())
                .discountAmount(full.getDiscountAmount())
                .deliveryCharges(full.getDeliveryCharges())
                .totalAmount(full.getTotalAmount())
                .specialInstructions(full.getSpecialInstructions())
                .scheduledAt(full.getScheduledAt())
                .confirmedAt(full.getConfirmedAt())
                .dispatchedAt(full.getDispatchedAt())
                .deliveredAt(full.getDeliveredAt())
                .cancelledAt(full.getCancelledAt())
                .cancellationReason(full.getCancellationReason())
                .orderItems(full.getOrderItems())
                .deliveryAddress(full.getDeliveryAddress())
                .createdAt(full.getCreatedAt())
                .updatedAt(full.getUpdatedAt())
                .build();
    }
}
