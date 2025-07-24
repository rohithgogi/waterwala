package orderservice.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import orderservice.model.DeliveryType;
import orderservice.model.OrderType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderCreateRequest {
    @NotNull
    private Long customerId;

    @NotNull
    private Long businessId;

    @NotNull
    private OrderType orderType;

    @NotNull
    private DeliveryType deliveryType;

    @NotEmpty
    private List<CreateOrderItemRequest> items;

    @NotNull
    @Positive
    private BigDecimal subtotal;

    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal deliveryCharges;

    @NotNull
    @Positive
    private BigDecimal totalAmount;

    private String specialInstructions;
    private LocalDateTime scheduledAt;

    @NotNull
    private CreateDeliveryAddressRequest deliveryAddress;
}
