package paymentservice.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailsResponse {
    private Long id;
    private String orderNumber;
    private Long customerId;
    private Long businessId;
    private BigDecimal totalAmount;
    private String status;
    private String paymentStatus;
}