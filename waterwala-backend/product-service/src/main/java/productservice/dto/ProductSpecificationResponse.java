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
@Schema(description = "Product specification details")
public class ProductSpecificationResponse {

    @Schema(description = "Specification identifier")
    private String id;

    @Schema(description = "Specification key", example = "Volume")
    private String specKey;

    @Schema(description = "Specification value", example = "20")
    private String specValue;

    @Schema(description = "Unit of measurement", example = "Liters")
    private String unit;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;
}
