package productservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Product inventory information")
public class ProductInventoryResponse {

    @Schema(description = "Current available stock", example = "85")
    private Integer currentStock;

    @Schema(description = "Reserved stock", example = "15")
    private Integer reservedStock;

    @Schema(description = "Minimum stock level", example = "10")
    private Integer minStockLevel;

    @Schema(description = "Maximum stock level", example = "500")
    private Integer maxStockLevel;

    @Schema(description = "Reorder point", example = "20")
    private Integer reorderPoint;

    @Schema(description = "Reorder quantity", example = "100")
    private Integer reorderQuantity;

    @Schema(description = "Warehouse location", example = "Warehouse-A")
    private String warehouseLocation;

    @Schema(description = "Last inventory update timestamp")
    private LocalDateTime lastUpdated;
}