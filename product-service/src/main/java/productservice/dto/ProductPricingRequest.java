package productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductPricingRequest {

    @NotNull(message = "Minimum quantity is required")
    @Min(value = 1, message = "Minimum quantity must be at least 1")
    private Integer minQuantity;

    @NotNull(message = "Maximum quantity is required")
    @Min(value = 1, message = "Maximum quantity must be at least 1")
    private Integer maxQuantity;

    @NotNull(message = "Price per unit is required")
    @DecimalMin(value = "0.01", message = "Price per unit must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Invalid price format")
    private BigDecimal pricePerUnit;

    @DecimalMin(value = "0.00", message = "Discount percentage cannot be negative")
    @DecimalMax(value = "100.00", message = "Discount percentage cannot exceed 100")
    @Digits(integer = 3, fraction = 2, message = "Invalid discount percentage format")
    private BigDecimal discountPercentage;
}
