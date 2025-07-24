package orderservice.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_tracking")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false, length = 500)
    private String description;

    @Column
    private String remarks;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column
    private Long updatedBy; // user/business/delivery person ID

    @Enumerated(EnumType.STRING)
    private UpdatedByType updatedByType;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
