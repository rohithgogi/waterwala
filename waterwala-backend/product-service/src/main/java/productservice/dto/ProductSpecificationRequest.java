package productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSpecificationRequest {

    @NotBlank(message = "Specification key is required")
    private String specKey;

    @NotBlank(message = "Specification value is required")
    private String specValue;

    private String unit;
}