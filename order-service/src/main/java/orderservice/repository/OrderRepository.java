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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByCustomerId(Long customerId);

    Page<Order> findByCustomerId(Long customerId, Pageable pageable);

    List<Order> findByBusinessId(Long businessId);

    Page<Order> findByBusinessId(Long businessId, Pageable pageable);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByPaymentStatus(PaymentStatus paymentStatus);

    @Query("SELECT o FROM Order o WHERE o.status= :status AND o.customerId= :customerId")
    List<Order> findByCustomerIdAndStatus(@Param("customerId") Long customerId, @Param("status") OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.businessId = :businessId AND o.status = :status")
    List<Order> findByBusinessIdAndStatus(@Param("businessId") Long businessId, @Param("Status") OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findOrdersBetweenDates(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    @Query("SELECT o FROM Order o WHERE o.businessId = :businessId AND o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findBusinessOrdersBetweenDates(@Param("businessId") Long businessId,
                                               @Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.businessId = :businessId AND o.status = :status")
    Long countByBusinessIdAndStatus(@Param("businessId") Long businessId,
                                    @Param("status") OrderStatus status);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.businessId = :businessId AND o.status = 'DELIVERED'")
    Double getTotalRevenueByBusinessId(@Param("businessId") Long businessId);


}
