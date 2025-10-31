package paymentservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Payment Reference cannot be null")
    private String paymentReference;

    @Column(nullable = false)
    @NotNull(message = "Order ID cannot be null")
    @Positive(message = "Order ID must be positive")
    private Long orderId;

    @Column(nullable = false)
    @NotNull(message = "Customer ID cannot be null")
    @Positive(message = "Customer ID must be positive")
    private Long customerId;

    @Column(nullable = false)
    @NotNull(message = "Business ID cannot be null")
    @Positive(message = "Business ID must be positive")
    private Long businessId;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be positive")
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    @NotBlank(message = "Currency cannot be blank")
    private String currency; // INR, USD, etc.

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Payment method cannot be null")
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Payment status cannot be null")
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    // Razorpay specific fields
    @Column(unique = true)
    private String razorpayOrderId;

    @Column(unique = true)
    private String razorpayPaymentId;

    @Column
    private String razorpaySignature;

    // Transaction details
    @Column(length = 500)
    private String description;

    @Column(length = 1000)
    private String failureReason;

    @Column
    private LocalDateTime paidAt;

    @Column
    private LocalDateTime failedAt;

    @Column
    private LocalDateTime refundedAt;

    @Column(precision = 10, scale = 2)
    private BigDecimal refundedAmount;

    @Column(length = 500)
    private String refundReason;

    // Metadata
    @Column(length = 50)
    private String customerEmail;

    @Column(length = 20)
    private String customerPhone;

    @Column(length = 100)
    private String ipAddress;

    @Column(length = 500)
    private String userAgent;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Helper methods
    public boolean isPending() {
        return this.status == PaymentStatus.PENDING;
    }

    public boolean isSuccessful() {
        return this.status == PaymentStatus.COMPLETED;
    }

    public boolean isFailed() {
        return this.status == PaymentStatus.FAILED;
    }

    public boolean canBeRefunded() {
        return this.status == PaymentStatus.COMPLETED &&
                (this.refundedAmount == null ||
                        this.refundedAmount.compareTo(this.amount) < 0);
    }
}