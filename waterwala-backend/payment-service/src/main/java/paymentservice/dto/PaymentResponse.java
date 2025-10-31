package paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import paymentservice.model.PaymentMethod;
import paymentservice.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private Long id;
    private String paymentReference;
    private Long orderId;
    private Long customerId;
    private Long businessId;
    private BigDecimal amount;
    private String currency;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpayKeyId; // For frontend integration
    private String description;
    private String failureReason;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
}