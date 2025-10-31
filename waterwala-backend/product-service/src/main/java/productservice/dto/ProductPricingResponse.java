package productservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Product pricing tier information")
public class ProductPricingResponse {

    @Schema(description = "Pricing tier identifier")
    private String id;

    @Schema(description = "Minimum quantity for this tier", example = "10")
    private Integer minQuantity;

    @Schema(description = "Maximum quantity for this tier", example = "50")
    private Integer maxQuantity;

    @Schema(description = "Price per unit for this tier", example = "45.00")
    private BigDecimal pricePerUnit;

    @Schema(description = "Discount percentage", example = "10.00")
    private BigDecimal discountPercentage;

    @Schema(description = "Pricing tier active status", example = "true")
    private Boolean isActive;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;
}