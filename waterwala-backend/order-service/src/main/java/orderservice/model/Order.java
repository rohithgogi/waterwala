package orderservice.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Order number cannot be blank")
    private String orderNumber;

    @Column(nullable = false)
    @NotNull(message = "Customer ID cannot be null")
    @Positive(message = "Customer ID must be positive")
    private Long customerId;

    @Column(nullable = false)
    @NotNull(message = "Business ID cannot be null")
    @Positive(message = "Business ID must be positive")
    private Long businessId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Order type cannot be null")
    private OrderType orderType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Order status cannot be null")
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Payment status cannot be null")
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Delivery type cannot be null")
    private DeliveryType deliveryType;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Subtotal cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Subtotal must be positive")
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Tax amount cannot be null")
    @DecimalMin(value = "0.0", message = "Tax amount must be non-negative")
    private BigDecimal taxAmount;

    @Column(precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "Discount amount must be non-negative")
    private BigDecimal discountAmount;

    @Column(precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "Delivery charges must be non-negative")
    private BigDecimal deliveryCharges;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Total amount cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be positive")
    private BigDecimal totalAmount;

    @Column(length = 500)
    @Size(max = 500, message = "Special instructions cannot exceed 500 characters")
    private String specialInstructions;

    @Column
    @Future(message = "Scheduled time must be in the future")
    private LocalDateTime scheduledAt;

    @Column
    private LocalDateTime confirmedAt;

    @Column
    private LocalDateTime dispatchedAt;

    @Column
    private LocalDateTime deliveredAt;

    @Column
    private LocalDateTime cancelledAt;

    @Column(length = 500)
    @Size(max = 500, message = "Cancellation reason cannot exceed 500 characters")
    private String cancellationReason;

    @Column
    @Positive(message = "Delivery person ID must be positive")
    private Long assignedDeliveryPersonId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @NotEmpty(message = "Order must contain at least one item")
    private List<OrderItem> orderItems;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @NotNull(message = "Delivery address is required")
    private OrderDeliveryAddress deliveryAddress;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Business logic methods for order status validation
    public boolean canBeConfirmed() {
        return this.status == OrderStatus.PENDING;
    }

    public boolean canBeDispatched() {
        return this.status == OrderStatus.CONFIRMED;
    }

    public boolean canBeDelivered() {
        return this.status == OrderStatus.OUT_FOR_DELIVERY;
    }

    public boolean canBeCancelled() {
        return this.status != OrderStatus.DELIVERED && this.status != OrderStatus.CANCELLED;
    }
}