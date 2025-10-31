package orderservice.repository;

import orderservice.model.Order;
import orderservice.model.OrderStatus;
import orderservice.model.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    // Customer related queries - consistent naming
    List<Order> findByCustomerId(Long customerId);
    Page<Order> findByCustomerId(Long customerId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.customerId = :customerId")
    List<Order> findByCustomerIdAndStatus(@Param("customerId") Long customerId, @Param("status") OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.customerId = :customerId")
    Page<Order> findByCustomerIdAndStatus(@Param("customerId") Long customerId, @Param("status") OrderStatus status, Pageable pageable);

    // Business related queries - consistent naming
    List<Order> findByBusinessId(Long businessId);
    Page<Order> findByBusinessId(Long businessId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.businessId = :businessId AND o.status = :status")
    List<Order> findByBusinessIdAndStatus(@Param("businessId") Long businessId, @Param("status") OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.businessId = :businessId AND o.status = :status")
    Page<Order> findByBusinessIdAndStatus(@Param("businessId") Long businessId, @Param("status") OrderStatus status, Pageable pageable);

    // Status and payment queries
    List<Order> findByStatus(OrderStatus status);
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    List<Order> findByPaymentStatus(PaymentStatus paymentStatus);
    Page<Order> findByPaymentStatus(PaymentStatus paymentStatus, Pageable pageable);

    // Date range queries with null safety
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findOrdersBetweenDates(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    @Query("SELECT o FROM Order o WHERE o.businessId = :businessId AND o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findBusinessOrdersBetweenDates(@Param("businessId") Long businessId,
                                               @Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);

    // Analytics queries - updated return types for precision
    @Query("SELECT COUNT(o) FROM Order o WHERE o.businessId = :businessId AND o.status = :status")
    Long countByBusinessIdAndStatus(@Param("businessId") Long businessId,
                                    @Param("status") OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.businessId = :businessId AND o.status = 'DELIVERED'")
    BigDecimal getTotalRevenueByBusinessId(@Param("businessId") Long businessId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.customerId = :customerId")
    Long countByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT COALESCE(AVG(o.totalAmount), 0) FROM Order o WHERE o.businessId = :businessId AND o.status = 'DELIVERED'")
    BigDecimal getAverageOrderValueByBusinessId(@Param("businessId") Long businessId);
}