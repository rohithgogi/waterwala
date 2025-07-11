package productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductPricingResponse {

    private Long id;
    private Integer minQuantity;
    private Integer maxQuantity;
    private BigDecimal pricePerUnit;
    private BigDecimal discountPercentage;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
