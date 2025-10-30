package paymentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import paymentservice.model.Payment;
import paymentservice.model.PaymentStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentReference(String paymentReference);

    Optional<Payment> findByStripePaymentIntentId(String stripePaymentIntentId);

    List<Payment> findByOrderId(Long orderId);

    List<Payment> findByCustomerId(Long customerId);

    List<Payment> findByCustomerIdAndStatus(Long customerId, PaymentStatus status);

    List<Payment> findByBusinessId(Long businessId);

    boolean existsByOrderIdAndStatus(Long orderId, PaymentStatus status);

}
