package orderservice.utility;

import orderservice.dto.*;
import orderservice.model.Order;
import orderservice.model.OrderDeliveryAddress;
import orderservice.model.OrderItem;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring",
unmappedTargetPolicy = ReportingPolicy.IGNORE,
nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderMapper {

    // Order mappings
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderNumber", ignore = true) // Will be set by service
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "paymentStatus", constant = "PENDING")
    @Mapping(target = "confirmedAt", ignore = true)
    @Mapping(target = "dispatchedAt", ignore = true)
    @Mapping(target = "deliveredAt", ignore = true)
    @Mapping(target = "cancelledAt", ignore = true)
    @Mapping(target = "cancellationReason", ignore = true)
    @Mapping(target = "assignedDeliveryPersonId", ignore = true)
    @Mapping(target = "orderItems", ignore = true) // Will be handled separately
    @Mapping(target = "deliveryAddress", ignore = true) // Will be handled separately
    @Mapping(target = "createdAt", ignore = true) // Auto-generated
    @Mapping(target = "updatedAt", ignore = true) // Auto-generated
    Order toEntity(OrderCreateRequest request);

    // Response mapping with nested objects
    @Mapping(source = "orderItems", target = "orderItems")
    @Mapping(source = "deliveryAddress", target = "deliveryAddress")
    OrderResponse toResponse(Order order);

    // List mapping
    List<OrderResponse> toResponseList(List<Order> orders);

    // OrderItem mappings
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true) // Will be set by service
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "createdAt", ignore = true) // Auto-generated
    OrderItem toItemEntity(CreateOrderItemRequest request);

    OrderItemResponse toItemResponse(OrderItem orderItem);

    List<OrderItem> toItemEntityList(List<CreateOrderItemRequest> requests);
    List<OrderItemResponse> toItemResponseList(List<OrderItem> orderItems);

    // DeliveryAddress mappings
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true) // Will be set by service
    @Mapping(target = "addressType", constant = "HOME") // Default value
    OrderDeliveryAddress toAddressEntity(CreateDeliveryAddressRequest request);

    OrderDeliveryAddressResponse toAddressResponse(OrderDeliveryAddress address);

    // Custom mapping methods for complex scenarios
    @AfterMapping
    default void setOrderItemsOrder(@MappingTarget Order order, OrderCreateRequest request) {
        if (order.getOrderItems() != null) {
            order.getOrderItems().forEach(item -> item.setOrder(order));
        }
    }

    @AfterMapping
    default void setDeliveryAddressOrder(@MappingTarget Order order, OrderCreateRequest request) {
        if (order.getDeliveryAddress() != null) {
            order.getDeliveryAddress().setOrder(order);
        }
    }
}