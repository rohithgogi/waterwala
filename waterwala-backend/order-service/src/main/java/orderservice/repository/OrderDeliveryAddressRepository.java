package orderservice.repository;

import orderservice.model.AddressType;
import orderservice.model.OrderDeliveryAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDeliveryAddressRepository extends JpaRepository<OrderDeliveryAddress, Long> {
    Optional<OrderDeliveryAddress> findByOrderId(Long orderId);

    List<OrderDeliveryAddress> findByRecipientPhone(String recipientPhone);

    List<OrderDeliveryAddress> findByPincode(String pincode);

    List<OrderDeliveryAddress> findByAddressType(AddressType addressType);

    @Query("SELECT oda FROM OrderDeliveryAddress oda WHERE oda.city = :city AND oda.pincode = :pincode")
    List<OrderDeliveryAddress> findByCityAndPincode(@Param("city") String city,
                                                    @Param("pincode") String pincode);

    @Query("SELECT DISTINCT oda.pincode FROM OrderDeliveryAddress oda WHERE oda.order.businessId = :businessId")
    List<String> findDeliveryAreasByBusinessId(@Param("businessId") Long businessId);

}
