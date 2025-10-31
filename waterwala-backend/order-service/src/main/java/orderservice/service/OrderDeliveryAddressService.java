package orderservice.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import orderservice.dto.CreateDeliveryAddressRequest;
import orderservice.dto.OrderDeliveryAddressResponse;
import orderservice.exceptions.OrderNotFoundException;
import orderservice.model.AddressType;
import orderservice.model.Order;
import orderservice.model.OrderDeliveryAddress;
import orderservice.repository.OrderDeliveryAddressRepository;
import orderservice.utility.OrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderDeliveryAddressService {

    private final OrderDeliveryAddressRepository deliveryAddressRepository;
    private final OrderMapper orderMapper;

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

    @Transactional(readOnly = true)
    public OrderDeliveryAddressResponse getDeliveryAddressByOrderId(Long orderId) {
        OrderDeliveryAddress address = deliveryAddressRepository.findByOrderId(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Delivery address not found for order ID: " + orderId));
        return orderMapper.toAddressResponse(address);
    }

    @Transactional(readOnly = true)
    public List<OrderDeliveryAddressResponse> getDeliveryAddressesByPhone(String phone) {
        List<OrderDeliveryAddress> addresses = deliveryAddressRepository.findByRecipientPhone(phone);
        return addresses.stream()
                .map(orderMapper::toAddressResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderDeliveryAddressResponse> getDeliveryAddressesByPincode(String pincode) {
        List<OrderDeliveryAddress> addresses = deliveryAddressRepository.findByPincode(pincode);
        return addresses.stream()
                .map(orderMapper::toAddressResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<String> getDeliveryAreasByBusiness(Long businessId) {
        return deliveryAddressRepository.findDeliveryAreasByBusinessId(businessId);
    }

    @Transactional
    public OrderDeliveryAddressResponse updateDeliveryAddress(Long addressId, CreateDeliveryAddressRequest request) {
        OrderDeliveryAddress address = deliveryAddressRepository.findById(addressId)
                .orElseThrow(() -> new OrderNotFoundException("Delivery address not found with ID: " + addressId));

        // Update fields
        address.setRecipientName(request.getRecipientName());
        address.setRecipientPhone(request.getRecipientPhone());
        address.setRecipientEmail(request.getRecipientEmail());
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPincode(request.getPincode());
        address.setLandmark(request.getLandmark());
        address.setLatitude(request.getLatitude());
        address.setLongitude(request.getLongitude());

        OrderDeliveryAddress savedAddress = deliveryAddressRepository.save(address);
        log.info("Updated delivery address for order: {}", address.getOrder().getOrderNumber());

        return orderMapper.toAddressResponse(savedAddress);
    }
}