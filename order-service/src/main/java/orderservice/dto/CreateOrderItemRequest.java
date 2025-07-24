package orderservice.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CreateOrderItemRequest {
    @NotNull
    private Long productId;

    @NotNull
    private String productName;

    @NotNull
    private String productSku;

    @NotNull
    @Positive
    private Integer quantity;

    @NotNull
    private String unit;

    @NotNull
    @Positive
    private BigDecimal unitPrice;

    private BigDecimal discountAmount;

    @NotNull
    @Positive
    private BigDecimal totalPrice;

    private String itemSpecifications;
    private LocalDateTime scheduledDeliveryTime;
}
