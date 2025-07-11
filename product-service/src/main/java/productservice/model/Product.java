package productservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import productservice.model.ProductCategory;
import productservice.model.ProductPricing;
import productservice.model.ProductSpecification;
import productservice.model.ProductType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String sku;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType type;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal discountedPrice;

    @Column(nullable = false)
    private Integer availableQuantity;

    @Column(nullable = false)
    private Integer minOrderQuantity;

    @Column(nullable = false)
    private Integer maxOrderQuantity;

    @Column(nullable = false)
    private String unit; // L, pieces, kg, etc.

    @Column(nullable = false)
    private Long businessId;

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false)
    private Boolean isAvailable;

    @Column
    private String brand;

    @Column
    private String imageUrl;

    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private List<String> additionalImages;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductSpecification> specifications;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductPricing> pricingTiers;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}