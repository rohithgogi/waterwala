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
public class ProductSpecificationResponse {

    private Long id;
    private String specKey;
    private String specValue;
    private String unit;
    private LocalDateTime createdAt;
}
