package orderservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "order_subscriptions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String subscriptionNumber;

    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false)
    private Long businessId;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionFrequency frequency;

    @Column(nullable = false)
    private LocalTime preferredDeliveryTime;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column
    private LocalDateTime endDate;

    @Column(nullable = false)
    private LocalDateTime nextDeliveryDate;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerDelivery;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status;

    @Column
    private String pauseReason;

    @Column
    private LocalDateTime pausedAt;

    @Column
    private LocalDateTime resumeAt;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "subscription_delivery_days", joinColumns = @JoinColumn(name = "subscription_id"))
    @Column(name = "day_of_week")
    private List<orderservice.model.DayOfWeek> deliveryDays;

    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> generatedOrders;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}