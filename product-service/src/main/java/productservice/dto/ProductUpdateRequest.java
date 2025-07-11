package productservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import productservice.model.ProductCategory;
import productservice.model.ProductType;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductUpdateRequest {
    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 255, message = "Product name must be between 2 and 255 characters")
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotBlank(message = "SKU is required")
    @Size(min = 3, max = 50, message = "SKU must be between 3 and 50 characters")
    private String sku;

    @NotNull(message = "Category is required")
    private ProductCategory category;

    @NotNull(message = "Type is required")
    private ProductType type;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.01", message = "Base price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Invalid price format")
    private BigDecimal basePrice;

    @DecimalMin(value = "0.01", message = "Discounted price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Invalid price format")
    private BigDecimal discountedPrice;

    @NotNull(message = "Available quantity is required")
    @Min(value = 0, message = "Available quantity cannot be negative")
    private Integer availableQuantity;

    @NotNull(message = "Minimum order quantity is required")
    @Min(value = 1, message = "Minimum order quantity must be at least 1")
    private Integer minOrderQuantity;

    @NotNull(message = "Maximum order quantity is required")
    @Min(value = 1, message = "Maximum order quantity must be at least 1")
    private Integer maxOrderQuantity;

    @NotBlank(message = "Unit is required")
    private String unit;

    private String brand;
    private String imageUrl;
    private List<String> additionalImages;
    private List<ProductSpecificationRequest> specifications;
    private List<ProductPricingRequest> pricingTiers;
}
