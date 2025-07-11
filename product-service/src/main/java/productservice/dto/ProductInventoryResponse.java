package productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductInventoryResponse {

    private Long id;
    private Integer currentStock;
    private Integer reservedStock;
    private Integer minStockLevel;
    private Integer maxStockLevel;
    private Integer reorderPoint;
    private Integer reorderQuantity;
    private String warehouseLocation;
    private LocalDateTime lastUpdated;
}