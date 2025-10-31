package orderservice.repository;

import orderservice.model.OrderItem;
import orderservice.model.OrderItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

    List<OrderItem> findByProductId(Long productId);

    List<OrderItem> findByStatus(OrderItemStatus status);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId AND oi.status = :status")
    List<OrderItem> findByOrderIdAndStatus(@Param("orderId") Long orderId, @Param("status") OrderItemStatus status);

    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.productId = :productId AND oi.status = 'DELIVERED'")
    Integer getTotalQuantitySoldByProduct(@Param("productId") Long productId);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.productId = :productId AND oi.order.businessId = :businessId")
    List<OrderItem> findByBusinessIdAndProductId(@Param("productId") Long productId,@Param("businessId") Long businessId);


}
