package orderservice.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import orderservice.dto.CreateOrderItemRequest;
import orderservice.model.Order;
import orderservice.model.OrderItem;
import orderservice.model.OrderItemStatus;
import orderservice.repository.OrderItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderItemService {
    private final OrderItemRepository repository;

    @Transactional
    public void createOrderItems(Order order, List<CreateOrderItemRequest> itemRequests){
        List<OrderItem> orderItems = itemRequests.stream()
                .map(request -> OrderItem.builder()
                        .order(order)
                        .productId(request.getProductId())
                        .productName(request.getProductName())
                        .productSku(request.getProductSku())
                        .quantity(request.getQuantity())
                        .unit(request.getUnit())
                        .unitPrice(request.getUnitPrice())
                        .discountAmount(request.getDiscountAmount())
                        .totalPrice(request.getTotalPrice())
                        .itemSpecifications(request.getItemSpecifications())
                        .scheduledDeliveryTime(request.getScheduledDeliveryTime())
                        .status(OrderItemStatus.PENDING)
                        .build())
                .collect(Collectors.toList());
        repository.saveAll(orderItems);
        log.info("Created {} order items for order: {}", orderItems.size(), order.getOrderNumber());
    }

    @Transactional
    public void markItemsAsDelivered(Long orderId){
        List<OrderItem> items = repository.findByOrderId(orderId);
        items.forEach(item->item.setStatus(OrderItemStatus.DELIVERED));
        repository.saveAll(items);
    }

    public List<OrderItem> getOrderItems(Long orderId) {
        return repository.findByOrderId(orderId);
    }


}
