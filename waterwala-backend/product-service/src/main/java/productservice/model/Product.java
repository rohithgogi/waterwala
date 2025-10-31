package productservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    private String id;

    private String name;
    private String description;

    @Indexed(unique = true)
    private String sku;

    private ProductCategory category;
    private ProductType type;

    private BigDecimal basePrice;
    private BigDecimal discountedPrice;

    private Integer availableQuantity;
    private Integer minOrderQuantity;
    private Integer maxOrderQuantity;

    private String unit;

    // CHANGED: businessId from String to Long
    @Indexed
    private Long businessId;

    private Boolean isActive;
    private Boolean isAvailable;
    private String brand;
    private String imageUrl;
    private List<String> additionalImages;

    // Embedded documents
    private List<ProductSpecification> specifications;
    private List<ProductPricing> pricingTiers;
    private ProductInventory inventory;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Embedded classes
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductSpecification {
        private String specKey;
        private String specValue;
        private String unit;
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductPricing {
        private Integer minQuantity;
        private Integer maxQuantity;
        private BigDecimal pricePerUnit;
        private BigDecimal discountPercentage;
        private Boolean isActive;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductInventory {
        private Integer currentStock;
        private Integer reservedStock;
        private Integer minStockLevel;
        private Integer maxStockLevel;
        private Integer reorderPoint;
        private Integer reorderQuantity;
        private String warehouseLocation;
        private LocalDateTime lastUpdated;
    }
}