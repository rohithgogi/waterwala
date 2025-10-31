package paymentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import paymentservice.model.PaymentTransaction;

import java.util.List;
import java.util.Optional;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    List<PaymentTransaction> findByPaymentId(Long paymentId);

    Optional<PaymentTransaction> findByStripeTransactionId(String stripeTransactionId);
}
