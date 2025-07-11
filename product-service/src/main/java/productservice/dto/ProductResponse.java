package productservice.dto;

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
public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private String sku;
    private ProductCategory category;
    private ProductType type;
    private BigDecimal basePrice;
    private BigDecimal discountedPrice;
    private Integer availableQuantity;
    private Integer minOrderQuantity;
    private Integer maxOrderQuantity;
    private String unit;
    private Long businessId;
    private Boolean isActive;
    private Boolean isAvailable;
    private String brand;
    private String imageUrl;
    private List<String> additionalImages;
    private List<ProductSpecificationResponse> specifications;
    private List<ProductPricingResponse> pricingTiers;
    private ProductInventoryResponse inventory;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
