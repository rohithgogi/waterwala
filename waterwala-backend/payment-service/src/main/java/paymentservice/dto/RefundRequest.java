package paymentservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequest {

    @NotNull(message = "Payment ID is required")
    @Positive(message = "Payment ID must be positive")
    private Long paymentId;

    @DecimalMin(value = "0.01", message = "Refund amount must be positive")
    private BigDecimal amount; // If null, full refund

    @Size(max = 500, message = "Reason cannot exceed 500 characters")
    private String reason;
}
