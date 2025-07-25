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

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderNumberGenerator orderNumberGenerator;

    public OrderResponse createOrder(OrderCreateRequest request){
        log.info("Creating order for customer: {}", request.getCustomerId());

        Order order=orderMapper.toEntity(request);

        //set fields that mapper ignores
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

    public OrderResponse getOrderById(Long id){
        Order order=orderRepository.findById(id)
                .orElseThrow(()->new OrderNotFoundException("Order not found with id:"+id));
        return orderMapper.toResponse(order);
    }

    public OrderResponse getOrderByNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with number: " + orderNumber));
        return orderMapper.toResponse(order);
    }

    public List<OrderResponse> getOrdersByCustomer(Long customerId) {
        List<Order> orders = orderRepository.findByCustomerId(customerId);
        return orderMapper.toResponseList(orders);
    }

    public Page<OrderResponse> getOrdersByCustomer(Long customerId, Pageable pageable) {
        Page<Order> orders = orderRepository.findByCustomerId(customerId, pageable);
        return orders.map(orderMapper::toResponse);
    }

    public List<OrderResponse> getOrdersByBusiness(Long businessId) {
        List<Order> orders = orderRepository.findByBusinessId(businessId);
        return orderMapper.toResponseList(orders);
    }
    public Page<OrderResponse> getOrdersByBusiness(Long businessId, Pageable pageable) {
        Page<Order> orders = orderRepository.findByBusinessId(businessId, pageable);
        return orders.map(orderMapper::toResponse);
    }

    @Transactional
    public OrderResponse confirmOrder(Long orderId) {
        Order order = getOrderEntity(orderId);

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderStatusException("Order cannot be confirmed in current status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CONFIRMED);
        order.setConfirmedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);
        log.info("Order confirmed: {}", savedOrder.getOrderNumber());

        return orderMapper.toResponse(savedOrder);
    }

    @Transactional
    public OrderResponse dispatchOrder(Long orderId, Long deliveryPersonId) {
        Order order = getOrderEntity(orderId);

        if (order.getStatus() != OrderStatus.CONFIRMED) {
            throw new InvalidOrderStatusException("Order cannot be dispatched in current status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
        order.setDispatchedAt(LocalDateTime.now());
        order.setAssignedDeliveryPersonId(deliveryPersonId);

        Order savedOrder = orderRepository.save(order);
        log.info("Order dispatched: {}", savedOrder.getOrderNumber());

        return orderMapper.toResponse(savedOrder);
    }

    @Transactional
    public OrderResponse deliverOrder(Long orderId) {
        Order order = getOrderEntity(orderId);

        if (order.getStatus() != OrderStatus.OUT_FOR_DELIVERY) {
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

    @Transactional
    public OrderResponse cancelOrder(Long orderId, String cancellationReason) {
        Order order = getOrderEntity(orderId);

        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new InvalidOrderStatusException("Order cannot be cancelled in current status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(LocalDateTime.now());
        order.setCancellationReason(cancellationReason);

        Order savedOrder = orderRepository.save(order);
        log.info("Order cancelled: {}", savedOrder.getOrderNumber());

        return orderMapper.toResponse(savedOrder);
    }

    public List<OrderResponse> getOrdersByStatus(OrderStatus status) {
        List<Order> orders = orderRepository.findByStatus(status);
        return orderMapper.toResponseList(orders);
    }

    public Long getBusinessOrderCount(Long businessId, OrderStatus status) {
        return orderRepository.countByBusinessIdAndStatus(businessId, status);
    }

    public Double getBusinessRevenue(Long businessId) {
        return orderRepository.getTotalRevenueByBusinessId(businessId);
    }

    private Order getOrderEntity(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));
    }
}
