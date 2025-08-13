package productservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import productservice.model.ProductCategory;
import productservice.model.ProductType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Product response containing all product details")
public class ProductResponse {

    @Schema(description = "Unique product identifier", example = "64f1b2c3d4e5f6789abcdef0")
    private String id;

    @Schema(description = "Product name", example = "Premium Water Can 20L")
    private String name;

    @Schema(description = "Product description", example = "High-quality purified water in 20L container")
    private String description;

    @Schema(description = "Stock Keeping Unit", example = "PWC-20L-001")
    private String sku;

    @Schema(description = "Product category")
    private ProductCategory category;

    @Schema(description = "Product type")
    private ProductType type;

    @Schema(description = "Base price", example = "50.00")
    private BigDecimal basePrice;

    @Schema(description = "Discounted price", example = "45.00")
    private BigDecimal discountedPrice;

    @Schema(description = "Available quantity", example = "100")
    private Integer availableQuantity;

    @Schema(description = "Minimum order quantity", example = "1")
    private Integer minOrderQuantity;

    @Schema(description = "Maximum order quantity", example = "50")
    private Integer maxOrderQuantity;

    @Schema(description = "Unit of measurement", example = "pieces")
    private String unit;

    @Schema(description = "Business identifier", example = "business_123")
    private String businessId;

    @Schema(description = "Product active status", example = "true")
    private Boolean isActive;

    @Schema(description = "Product availability status", example = "true")
    private Boolean isAvailable;

    @Schema(description = "Brand name", example = "AquaPure")
    private String brand;

    @Schema(description = "Primary image URL")
    private String imageUrl;

    @Schema(description = "Additional image URLs")
    private List<String> additionalImages;

    @Schema(description = "Product specifications")
    private List<ProductSpecificationResponse> specifications;

    @Schema(description = "Pricing tiers for bulk orders")
    private List<ProductPricingResponse> pricingTiers;

    @Schema(description = "Inventory information")
    private ProductInventoryResponse inventory;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;
}