package paymentservice.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusUpdateRequest {
    private String orderStatus;    // CONFIRMED, CANCELLED, etc.
    private String paymentStatus;  // COMPLETED, FAILED, PENDING
}
