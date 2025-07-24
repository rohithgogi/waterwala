package orderservice.repository;

import orderservice.model.OrderPayment;
import orderservice.model.PaymentMethod;
import orderservice.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderPaymentRepository extends JpaRepository<OrderPayment, Long> {

    List<OrderPayment> findByOrderId(Long orderId);

    Optional<OrderPayment> findByTransactionId(String transactionId);

    List<OrderPayment> findByStatus(PaymentStatus status);

    List<OrderPayment> findByPaymentMethod(PaymentMethod paymentMethod);

    @Query("SELECT op FROM OrderPayment op WHERE op.order.id = :orderId AND op.status = :status")
    List<OrderPayment> findByOrderIdAndStatus(@Param("orderId") Long orderId,
                                              @Param("status") PaymentStatus status);

    @Query("SELECT SUM(op.amount) FROM OrderPayment op WHERE op.order.id = :orderId AND op.status = 'COMPLETED'")
    BigDecimal getTotalPaidAmountByOrderId(@Param("orderId") Long orderId);

    @Query("SELECT op FROM OrderPayment op WHERE op.order.businessId = :businessId AND op.status = 'COMPLETED'")
    List<OrderPayment> findCompletedPaymentsByBusinessId(@Param("businessId") Long businessId);

    @Query("SELECT SUM(op.amount) FROM OrderPayment op WHERE op.order.businessId = :businessId AND op.status = 'COMPLETED'")
    BigDecimal getTotalRevenueByBusinessId(@Param("businessId") Long businessId);
}