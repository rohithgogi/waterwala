package orderservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import orderservice.dto.OrderCreateRequest;
import orderservice.dto.OrderResponse;
import orderservice.exceptions.InvalidOrderStatusException;
import orderservice.exceptions.OrderNotFoundException;
import orderservice.model.Order;
import orderservice.model.OrderDeliveryAddress;
import orderservice.model.OrderItem;
import orderservice.model.OrderStatus;
import orderservice.repository.OrderRepository;
import orderservice.utility.OrderMapper;
import orderservice.utility.OrderNumberGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
@Validated
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderNumberGenerator orderNumberGenerator;

    public OrderResponse createOrder(@Valid OrderCreateRequest request) {
        log.info("Creating order for customer: {}", request.getCustomerId());

        // Business validation
        validateOrderRequest(request);

        Order order = orderMapper.toEntity(request);

        // Set fields that mapper ignores
        order.setOrderNumber(orderNumberGenerator.generate());

        // Create and set order items
        List<OrderItem> orderItems = orderMapper.toItemEntityList(request.getItems());
        orderItems.forEach(item -> item.setOrder(order));
        order.setOrderItems(orderItems);

        // Create and set delivery address
        OrderDeliveryAddress deliveryAddress = orderMapper.toAddressEntity(request.getDeliveryAddress());
        deliveryAddress.setOrder(order);
        order.setDeliveryAddress(deliveryAddress);

        // Save order with all relationships
        Order savedOrder = orderRepository.save(order);

        log.info("Order created successfully with number: {}", savedOrder.getOrderNumber());
        return orderMapper.toResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(@NotNull @Positive Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));
        return orderMapper.toResponse(order);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderByNumber(@NotNull String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with number: " + orderNumber));
        return orderMapper.toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByCustomer(@NotNull @Positive Long customerId) {
        List<Order> orders = orderRepository.findByCustomerId(customerId);
        return orderMapper.toResponseList(orders);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByCustomer(@NotNull @Positive Long customerId, Pageable pageable) {
        Page<Order> orders = orderRepository.findByCustomerId(customerId, pageable);
        return orders.map(orderMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByBusiness(@NotNull @Positive Long businessId) {
        List<Order> orders = orderRepository.findByBusinessId(businessId);
        return orderMapper.toResponseList(orders);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByBusiness(@NotNull @Positive Long businessId, Pageable pageable) {
        Page<Order> orders = orderRepository.findByBusinessId(businessId, pageable);
        return orders.map(orderMapper::toResponse);
    }

    public OrderResponse confirmOrder(@NotNull @Positive Long orderId) {
        Order order = getOrderEntity(orderId);

        if (!order.canBeConfirmed()) {
            throw new InvalidOrderStatusException("Order cannot be confirmed in current status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CONFIRMED);
        order.setConfirmedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);
        log.info("Order confirmed: {}", savedOrder.getOrderNumber());

        return orderMapper.toResponse(savedOrder);
    }

    public OrderResponse dispatchOrder(@NotNull @Positive Long orderId, @NotNull @Positive Long deliveryPersonId) {
        Order order = getOrderEntity(orderId);

        if (!order.canBeDispatched()) {
            throw new InvalidOrderStatusException("Order cannot be dispatched in current status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
        order.setDispatchedAt(LocalDateTime.now());
        order.setAssignedDeliveryPersonId(deliveryPersonId);

        Order savedOrder = orderRepository.save(order);
        log.info("Order dispatched: {}", savedOrder.getOrderNumber());

        return orderMapper.toResponse(savedOrder);
    }

    public OrderResponse deliverOrder(@NotNull @Positive Long orderId) {
        Order order = getOrderEntity(orderId);

        if (!order.canBeDelivered()) {
            throw new InvalidOrderStatusException("Order cannot be delivered in current status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.DELIVERED);
        order.setDeliveredAt(LocalDateTime.now());

        // Update all order items status
        order.getOrderItems().forEach(item ->
                item.setStatus(orderservice.model.OrderItemStatus.DELIVERED));

        Order savedOrder = orderRepository.save(order);
        log.info("Order delivered: {}", savedOrder.getOrderNumber());

        return orderMapper.toResponse(savedOrder);
    }

    public OrderResponse cancelOrder(@NotNull @Positive Long orderId, @NotNull String cancellationReason) {
        Order order = getOrderEntity(orderId);

        if (!order.canBeCancelled()) {
            throw new InvalidOrderStatusException("Order cannot be cancelled in current status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(LocalDateTime.now());
        order.setCancellationReason(cancellationReason);

        Order savedOrder = orderRepository.save(order);
        log.info("Order cancelled: {}", savedOrder.getOrderNumber());

        return orderMapper.toResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByStatus(@NotNull OrderStatus status) {
        List<Order> orders = orderRepository.findByStatus(status);
        return orderMapper.toResponseList(orders);
    }

    @Transactional(readOnly = true)
    public Long getBusinessOrderCount(@NotNull @Positive Long businessId, @NotNull OrderStatus status) {
        return orderRepository.countByBusinessIdAndStatus(businessId, status);
    }

    @Transactional(readOnly = true)
    public BigDecimal getBusinessRevenue(@NotNull @Positive Long businessId) {
        return orderRepository.getTotalRevenueByBusinessId(businessId);
    }

    // Private helper methods
    private Order getOrderEntity(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));
    }

    private void validateOrderRequest(OrderCreateRequest request) {
        // Validate total amount calculation
        BigDecimal calculatedTotal = request.getSubtotal()
                .add(request.getTaxAmount() != null ? request.getTaxAmount() : BigDecimal.ZERO)
                .add(request.getDeliveryCharges() != null ? request.getDeliveryCharges() : BigDecimal.ZERO)
                .subtract(request.getDiscountAmount() != null ? request.getDiscountAmount() : BigDecimal.ZERO);

        if (calculatedTotal.compareTo(request.getTotalAmount()) != 0) {
            throw new InvalidOrderStatusException("Total amount calculation mismatch. Expected: " + calculatedTotal + ", Provided: " + request.getTotalAmount());
        }

        // Validate items total matches subtotal
        BigDecimal itemsTotal = request.getItems().stream()
                .map(item -> item.getTotalPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (itemsTotal.compareTo(request.getSubtotal()) != 0) {
            throw new InvalidOrderStatusException("Items total does not match subtotal. Items total: " + itemsTotal + ", Subtotal: " + request.getSubtotal());
        }

        // Validate each item's total price
        request.getItems().forEach(item -> {
            BigDecimal itemTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                    .subtract(item.getDiscountAmount() != null ? item.getDiscountAmount() : BigDecimal.ZERO);

            if (itemTotal.compareTo(item.getTotalPrice()) != 0) {
                throw new InvalidOrderStatusException("Item total price calculation error for product: " + item.getProductName());
            }
        });

        // Validate scheduled delivery time if provided
        if (request.getScheduledAt() != null && request.getScheduledAt().isBefore(LocalDateTime.now())) {
            throw new InvalidOrderStatusException("Scheduled delivery time cannot be in the past");
        }
    }
}
