package orderservice.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class OrderItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String productSku;
    private Integer quantity;
    private String unit;
    private BigDecimal unitPrice;
    private BigDecimal discountAmount;
    private BigDecimal totalPrice;
    private String itemSpecifications;
    private LocalDateTime scheduledDeliveryTime;
    private orderservice.model.OrderItemStatus status;
    private LocalDateTime createdAt;
}

