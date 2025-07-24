package orderservice.repository;

import orderservice.model.OrderStatus;
import orderservice.model.OrderTracking;
import orderservice.model.UpdatedByType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface OrderTrackingRepository extends JpaRepository<OrderTracking, Long> {
    List<OrderTracking> findByOrderIdOrderByCreatedDesc(Long orderId);

    List<OrderTracking> findByStatus(OrderStatus status);

    @Query("SELECT ot FROM OrderTracking ot WHERE ot.order.id = :orderId AND ot.status= :status ORDER BY ot.createdAt DESC")
    List<OrderTracking> findByOrderIdAndStatus(@Param("orderId")Long orderId, @Param("status") OrderStatus status);

    @Query("SELECT ot FROM OrderTracking ot WHERE ot.updatedBy= :updatedBy ORDER BY ot.createdAt DESC")
    List<OrderTracking> findByUpdatedBy(@Param("updatedBy") Long updatedBy);

    @Query("SELECT ot FROM OrderTracking ot WHERE ot.order.id = :orderId ORDER BY ot.createdAt ASC LIMIT 1")
    OrderTracking findFirstTrackingByOrderId(@Param("orderId") Long orderId);

    @Query("SELECT ot FROM OrderTracking ot WHERE ot.order.id = :orderId ORDER BY ot.createdAt DESC LIMIT 1")
    OrderTracking findLatestTrackingByOrderId(@Param("orderId") Long orderId);
}
