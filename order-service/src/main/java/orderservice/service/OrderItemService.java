package orderservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import orderservice.dto.CreateOrderItemRequest;
import orderservice.exceptions.InvalidOrderStatusException;
import orderservice.model.Order;
import orderservice.model.OrderItem;
import orderservice.model.OrderItemStatus;
import orderservice.repository.OrderItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
@Validated
public class OrderItemService {
    private final OrderItemRepository repository;

    @Transactional
    public void createOrderItems(@NotNull Order order, @NotEmpty @Valid List<CreateOrderItemRequest> itemRequests) {
        // Validate items before creation
        validateOrderItems(itemRequests);

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
    public void markItemsAsDelivered(@NotNull @Positive Long orderId) {
        List<OrderItem> items = repository.findByOrderId(orderId);
        if (items.isEmpty()) {
            throw new InvalidOrderStatusException("No items found for order ID: " + orderId);
        }

        items.forEach(item -> item.setStatus(OrderItemStatus.DELIVERED));
        repository.saveAll(items);
        log.info("Marked {} items as delivered for order ID: {}", items.size(), orderId);
    }

    @Transactional
    public void updateItemStatus(@NotNull @Positive Long itemId, @NotNull OrderItemStatus status) {
        OrderItem item = repository.findById(itemId)
                .orElseThrow(() -> new InvalidOrderStatusException("Order item not found with ID: " + itemId));

        item.setStatus(status);
        repository.save(item);
        log.info("Updated item {} status to: {}", itemId, status);
    }

    @Transactional(readOnly = true)
    public List<OrderItem> getOrderItems(@NotNull @Positive Long orderId) {
        return repository.findByOrderId(orderId);
    }

    @Transactional(readOnly = true)
    public Integer getTotalQuantitySold(@NotNull @Positive Long productId) {
        Integer total = repository.getTotalQuantitySoldByProduct(productId);
        return total != null ? total : 0;
    }

    private void validateOrderItems(List<CreateOrderItemRequest> items) {
        for (CreateOrderItemRequest item : items) {
            // Validate quantity is positive
            if (item.getQuantity() <= 0) {
                throw new InvalidOrderStatusException("Quantity must be positive for product: " + item.getProductName());
            }

            // Validate unit price is positive
            if (item.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidOrderStatusException("Unit price must be positive for product: " + item.getProductName());
            }

            // Validate total price calculation
            BigDecimal expectedTotal = item.getUnitPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()))
                    .subtract(item.getDiscountAmount() != null ? item.getDiscountAmount() : BigDecimal.ZERO);

            if (expectedTotal.compareTo(item.getTotalPrice()) != 0) {
                throw new InvalidOrderStatusException("Total price calculation error for product: " + item.getProductName());
            }

            // Validate discount doesn't exceed item total
            if (item.getDiscountAmount() != null &&
                    item.getDiscountAmount().compareTo(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))) > 0) {
                throw new InvalidOrderStatusException("Discount amount cannot exceed item total for product: " + item.getProductName());
            }
        }
    }
}
