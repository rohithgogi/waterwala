package productservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_inventory")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer currentStock;

    @Column(nullable = false)
    private Integer reservedStock;

    @Column(nullable = false)
    private Integer minStockLevel;

    @Column(nullable = false)
    private Integer maxStockLevel;

    @Column(nullable = false)
    private Integer reorderPoint;

    @Column(nullable = false)
    private Integer reorderQuantity;

    @Column
    private String warehouseLocation;

    @UpdateTimestamp
    private LocalDateTime lastUpdated;
}