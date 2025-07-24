package orderservice.repository;

import orderservice.model.OrderSubscription;
import orderservice.model.SubscriptionFrequency;
import orderservice.model.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface OrderSubscriptionRepository extends JpaRepository<OrderSubscription,Long> {
    Optional<OrderSubscription> findBySubscriptionNumber(String subscriptionNumber);

    List<OrderSubscription> findByCustomerId(Long customerId);

    List<OrderSubscription> findByBusinessId(Long businessId);

    List<OrderSubscription> findByStatus(SubscriptionStatus status);

    List<OrderSubscription> findByFrequency(SubscriptionFrequency frequency);

    @Query("SELECT os FROM OrderSubscription os WHERE os.customerId = :customerId AND  os.status= :status")
    List<OrderSubscription> findByCustomerIdAndStatus(@Param("customerId") Long customerId, @Param("status") SubscriptionStatus status);

    @Query("SELECT os FROM OrderSubscription os WHERE os.businessId = :businessId AND os.status = :status")
    List<OrderSubscription> findByBusinessIdAndStatus(@Param("businessId") Long businessId, @Param("status") SubscriptionStatus status);

    @Query("SELECT OS from OrderSubscription os WHERE os.nextDeliveryDate<= :date AND os.status='ACTIVE'")
    List<OrderSubscription> findSubscriptionDueForDelivery(@Param("date")LocalDateTime date);

    List<OrderSubscription> findExpiredSubscriptions(@Param("date") LocalDateTime date);

    @Query("SELECT os FROM OrderSubscription os WHERE os.resumeAt <= :date AND os.status = 'PAUSED'")
    List<OrderSubscription> findSubscriptionsToResume(@Param("date") LocalDateTime date);

    @Query("SELECT COUNT(os) FROM OrderSubscription os WHERE os.businessId = :businessId AND os.status = 'ACTIVE'")
    Long countActiveSubscriptionsByBusinessId(@Param("businessId") Long businessId);

}
