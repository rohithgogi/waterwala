package orderservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import orderservice.dto.CreateDeliveryAddressRequest;
import orderservice.model.AddressType;
import orderservice.model.Order;
import orderservice.model.OrderDeliveryAddress;
import orderservice.repository.OrderDeliveryAddressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderDeliveryAddressService {

    private final OrderDeliveryAddressRepository deliveryAddressRepository;

    @Transactional
    public void createDeliveryAddress(Order order, CreateDeliveryAddressRequest request) {
        OrderDeliveryAddress address = OrderDeliveryAddress.builder()
                .order(order)
                .recipientName(request.getRecipientName())
                .recipientPhone(request.getRecipientPhone())
                .recipientEmail(request.getRecipientEmail())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .landmark(request.getLandmark())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .addressType(AddressType.HOME) // Default for now
                .build();

        deliveryAddressRepository.save(address);
        log.info("Created delivery address for order: {}", order.getOrderNumber());
    }

}