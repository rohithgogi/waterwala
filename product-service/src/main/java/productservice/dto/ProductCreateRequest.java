package productservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request object for creating a new product")
public class ProductCreateRequest {

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 255, message = "Product name must be between 2 and 255 characters")
    @Schema(description = "Product name", example = "Premium Water Can 20L", required = true)
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Schema(description = "Product description", example = "High-quality purified water in 20L container")
    private String description;

    @NotBlank(message = "SKU is required")
    @Size(min = 3, max = 50, message = "SKU must be between 3 and 50 characters")
    @Schema(description = "Stock Keeping Unit", example = "PWC-20L-001", required = true)
    private String sku;

    @NotNull(message = "Category is required")
    @Schema(description = "Product category", required = true)
    private ProductCategory category;

    @NotNull(message = "Type is required")
    @Schema(description = "Product type", required = true)
    private ProductType type;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.01", message = "Base price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Invalid price format")
    @Schema(description = "Base price", example = "50.00", required = true)
    private BigDecimal basePrice;

    @DecimalMin(value = "0.01", message = "Discounted price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Invalid price format")
    @Schema(description = "Discounted price", example = "45.00")
    private BigDecimal discountedPrice;

    @NotNull(message = "Available quantity is required")
    @Min(value = 0, message = "Available quantity cannot be negative")
    @Schema(description = "Available quantity", example = "100", required = true)
    private Integer availableQuantity;

    @NotNull(message = "Minimum order quantity is required")
    @Min(value = 1, message = "Minimum order quantity must be at least 1")
    @Schema(description = "Minimum order quantity", example = "1", required = true)
    private Integer minOrderQuantity;

    @NotNull(message = "Maximum order quantity is required")
    @Min(value = 1, message = "Maximum order quantity must be at least 1")
    @Schema(description = "Maximum order quantity", example = "50", required = true)
    private Integer maxOrderQuantity;

    @NotBlank(message = "Unit is required")
    @Schema(description = "Unit of measurement", example = "pieces", required = true)
    private String unit;

    // CHANGED: businessId from String to Long to match Business Service
    @NotNull(message = "Business ID is required")
    @Schema(description = "Business identifier", example = "1", required = true)
    private Long businessId;

    @Schema(description = "Brand name", example = "AquaPure")
    private String brand;

    @Schema(description = "Primary image URL")
    private String imageUrl;

    @Schema(description = "Additional image URLs")
    private List<String> additionalImages;

    @Schema(description = "Product specifications")
    private List<ProductSpecificationRequest> specifications;

    @Schema(description = "Pricing tiers for bulk orders")
    private List<ProductPricingRequest> pricingTiers;

    // Inventory fields
    @NotNull(message = "Initial stock is required")
    @Min(value = 0, message = "Initial stock cannot be negative")
    @Schema(description = "Initial stock quantity", example = "100", required = true)
    private Integer initialStock;

    @NotNull(message = "Minimum stock level is required")
    @Min(value = 0, message = "Minimum stock level cannot be negative")
    @Schema(description = "Minimum stock level for alerts", example = "10", required = true)
    private Integer minStockLevel;

    @NotNull(message = "Maximum stock level is required")
    @Min(value = 1, message = "Maximum stock level must be at least 1")
    @Schema(description = "Maximum stock capacity", example = "500", required = true)
    private Integer maxStockLevel;

    @NotNull(message = "Reorder point is required")
    @Min(value = 0, message = "Reorder point cannot be negative")
    @Schema(description = "Stock level to trigger reorder", example = "20", required = true)
    private Integer reorderPoint;

    @NotNull(message = "Reorder quantity is required")
    @Min(value = 1, message = "Reorder quantity must be at least 1")
    @Schema(description = "Quantity to reorder", example = "100", required = true)
    private Integer reorderQuantity;

    @Schema(description = "Warehouse location", example = "Warehouse-A")
    private String warehouseLocation;
}