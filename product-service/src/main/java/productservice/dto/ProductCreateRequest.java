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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateRequest {

    @NotBlank
    @Size(min=2, max = 255)
    private String name;

    @Size(max = 1000)
    private  String description;

    @NotBlank(message = "Stock-keeping unit is required")
    @Size(min=3, max = 50)
    private String sku;

    @NotNull
    private ProductCategory category;

    @NotNull
    private ProductType type;

    @NotNull
    @DecimalMin(value = "0.01")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal basePrice;

    @NotNull
    @DecimalMin(value = "0.01")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal discountedPrice;

    @NotNull
    @Min(value = 0)
    private Integer availableQuantity;

    @NotNull
    @Min(value = 1)
    private Integer minOrderQuantity;

    @NotNull
    @Max(value = 1)
    private Integer maxOrderQuantity;

    @NotBlank(message = "Unit is required")
    private String unit;

    @NotNull(message = "Business ID is required")
    private Long businessId;

    private String brand;
    private String imageUrl;
    private List<String> additionalImages;
    private List<ProductSpecificationRequest> specifications;
    private List<ProductPricingRequest> pricingTiers;

    // Inventory fields
    @NotNull(message = "Initial stock is required")
    @Min(value = 0, message = "Initial stock cannot be negative")
    private Integer initialStock;

    @NotNull(message = "Minimum stock level is required")
    @Min(value = 0, message = "Minimum stock level cannot be negative")
    private Integer minStockLevel;

    @NotNull(message = "Maximum stock level is required")
    @Min(value = 1, message = "Maximum stock level must be at least 1")
    private Integer maxStockLevel;

    @NotNull(message = "Reorder point is required")
    @Min(value = 0, message = "Reorder point cannot be negative")
    private Integer reorderPoint;

    @NotNull(message = "Reorder quantity is required")
    @Min(value = 1, message = "Reorder quantity must be at least 1")
    private Integer reorderQuantity;

    private String warehouseLocation;



}
