package orderservice.utility;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import orderservice.dto.CreateDeliveryAddressRequest;
import orderservice.dto.CreateOrderItemRequest;
import orderservice.dto.OrderCreateRequest;
import orderservice.dto.OrderDeliveryAddressResponse;
import orderservice.dto.OrderItemResponse;
import orderservice.dto.OrderResponse;
import orderservice.model.Order;
import orderservice.model.OrderDeliveryAddress;
import orderservice.model.OrderItem;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-01T18:55:26+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class OrderMapperImpl implements OrderMapper {

    @Override
    public Order toEntity(OrderCreateRequest request) {
        if ( request == null ) {
            return null;
        }

        Order.OrderBuilder order = Order.builder();

        order.customerId( request.getCustomerId() );
        order.businessId( request.getBusinessId() );
        order.orderType( request.getOrderType() );
        order.deliveryType( request.getDeliveryType() );
        order.subtotal( request.getSubtotal() );
        order.taxAmount( request.getTaxAmount() );
        order.discountAmount( request.getDiscountAmount() );
        order.deliveryCharges( request.getDeliveryCharges() );
        order.totalAmount( request.getTotalAmount() );
        order.specialInstructions( request.getSpecialInstructions() );
        order.scheduledAt( request.getScheduledAt() );

        order.status( orderservice.model.OrderStatus.PENDING );
        order.paymentStatus( orderservice.model.PaymentStatus.PENDING );

        return order.build();
    }

    @Override
    public OrderResponse toResponse(Order order) {
        if ( order == null ) {
            return null;
        }

        OrderResponse.OrderResponseBuilder orderResponse = OrderResponse.builder();

        orderResponse.orderItems( toItemResponseList( order.getOrderItems() ) );
        orderResponse.deliveryAddress( toAddressResponse( order.getDeliveryAddress() ) );
        orderResponse.id( order.getId() );
        orderResponse.orderNumber( order.getOrderNumber() );
        orderResponse.customerId( order.getCustomerId() );
        orderResponse.businessId( order.getBusinessId() );
        orderResponse.orderType( order.getOrderType() );
        orderResponse.status( order.getStatus() );
        orderResponse.paymentStatus( order.getPaymentStatus() );
        orderResponse.deliveryType( order.getDeliveryType() );
        orderResponse.subtotal( order.getSubtotal() );
        orderResponse.taxAmount( order.getTaxAmount() );
        orderResponse.discountAmount( order.getDiscountAmount() );
        orderResponse.deliveryCharges( order.getDeliveryCharges() );
        orderResponse.totalAmount( order.getTotalAmount() );
        orderResponse.specialInstructions( order.getSpecialInstructions() );
        orderResponse.scheduledAt( order.getScheduledAt() );
        orderResponse.confirmedAt( order.getConfirmedAt() );
        orderResponse.dispatchedAt( order.getDispatchedAt() );
        orderResponse.deliveredAt( order.getDeliveredAt() );
        orderResponse.cancelledAt( order.getCancelledAt() );
        orderResponse.cancellationReason( order.getCancellationReason() );
        orderResponse.assignedDeliveryPersonId( order.getAssignedDeliveryPersonId() );
        orderResponse.createdAt( order.getCreatedAt() );
        orderResponse.updatedAt( order.getUpdatedAt() );

        return orderResponse.build();
    }

    @Override
    public List<OrderResponse> toResponseList(List<Order> orders) {
        if ( orders == null ) {
            return null;
        }

        List<OrderResponse> list = new ArrayList<OrderResponse>( orders.size() );
        for ( Order order : orders ) {
            list.add( toResponse( order ) );
        }

        return list;
    }

    @Override
    public OrderItem toItemEntity(CreateOrderItemRequest request) {
        if ( request == null ) {
            return null;
        }

        OrderItem.OrderItemBuilder orderItem = OrderItem.builder();

        orderItem.productId( request.getProductId() );
        orderItem.productName( request.getProductName() );
        orderItem.productSku( request.getProductSku() );
        orderItem.quantity( request.getQuantity() );
        orderItem.unit( request.getUnit() );
        orderItem.unitPrice( request.getUnitPrice() );
        orderItem.discountAmount( request.getDiscountAmount() );
        orderItem.totalPrice( request.getTotalPrice() );
        orderItem.itemSpecifications( request.getItemSpecifications() );
        orderItem.scheduledDeliveryTime( request.getScheduledDeliveryTime() );

        orderItem.status( orderservice.model.OrderItemStatus.PENDING );

        return orderItem.build();
    }

    @Override
    public OrderItemResponse toItemResponse(OrderItem orderItem) {
        if ( orderItem == null ) {
            return null;
        }

        OrderItemResponse.OrderItemResponseBuilder orderItemResponse = OrderItemResponse.builder();

        orderItemResponse.id( orderItem.getId() );
        orderItemResponse.productId( orderItem.getProductId() );
        orderItemResponse.productName( orderItem.getProductName() );
        orderItemResponse.productSku( orderItem.getProductSku() );
        orderItemResponse.quantity( orderItem.getQuantity() );
        orderItemResponse.unit( orderItem.getUnit() );
        orderItemResponse.unitPrice( orderItem.getUnitPrice() );
        orderItemResponse.discountAmount( orderItem.getDiscountAmount() );
        orderItemResponse.totalPrice( orderItem.getTotalPrice() );
        orderItemResponse.itemSpecifications( orderItem.getItemSpecifications() );
        orderItemResponse.scheduledDeliveryTime( orderItem.getScheduledDeliveryTime() );
        orderItemResponse.status( orderItem.getStatus() );
        orderItemResponse.createdAt( orderItem.getCreatedAt() );

        return orderItemResponse.build();
    }

    @Override
    public List<OrderItem> toItemEntityList(List<CreateOrderItemRequest> requests) {
        if ( requests == null ) {
            return null;
        }

        List<OrderItem> list = new ArrayList<OrderItem>( requests.size() );
        for ( CreateOrderItemRequest createOrderItemRequest : requests ) {
            list.add( toItemEntity( createOrderItemRequest ) );
        }

        return list;
    }

    @Override
    public List<OrderItemResponse> toItemResponseList(List<OrderItem> orderItems) {
        if ( orderItems == null ) {
            return null;
        }

        List<OrderItemResponse> list = new ArrayList<OrderItemResponse>( orderItems.size() );
        for ( OrderItem orderItem : orderItems ) {
            list.add( toItemResponse( orderItem ) );
        }

        return list;
    }

    @Override
    public OrderDeliveryAddress toAddressEntity(CreateDeliveryAddressRequest request) {
        if ( request == null ) {
            return null;
        }

        OrderDeliveryAddress.OrderDeliveryAddressBuilder orderDeliveryAddress = OrderDeliveryAddress.builder();

        orderDeliveryAddress.recipientName( request.getRecipientName() );
        orderDeliveryAddress.recipientPhone( request.getRecipientPhone() );
        orderDeliveryAddress.recipientEmail( request.getRecipientEmail() );
        orderDeliveryAddress.addressLine1( request.getAddressLine1() );
        orderDeliveryAddress.addressLine2( request.getAddressLine2() );
        orderDeliveryAddress.city( request.getCity() );
        orderDeliveryAddress.state( request.getState() );
        orderDeliveryAddress.pincode( request.getPincode() );
        orderDeliveryAddress.landmark( request.getLandmark() );
        orderDeliveryAddress.latitude( request.getLatitude() );
        orderDeliveryAddress.longitude( request.getLongitude() );

        orderDeliveryAddress.addressType( orderservice.model.AddressType.HOME );

        return orderDeliveryAddress.build();
    }

    @Override
    public OrderDeliveryAddressResponse toAddressResponse(OrderDeliveryAddress address) {
        if ( address == null ) {
            return null;
        }

        OrderDeliveryAddressResponse.OrderDeliveryAddressResponseBuilder orderDeliveryAddressResponse = OrderDeliveryAddressResponse.builder();

        orderDeliveryAddressResponse.id( address.getId() );
        orderDeliveryAddressResponse.recipientName( address.getRecipientName() );
        orderDeliveryAddressResponse.recipientPhone( address.getRecipientPhone() );
        orderDeliveryAddressResponse.recipientEmail( address.getRecipientEmail() );
        orderDeliveryAddressResponse.addressLine1( address.getAddressLine1() );
        orderDeliveryAddressResponse.addressLine2( address.getAddressLine2() );
        orderDeliveryAddressResponse.city( address.getCity() );
        orderDeliveryAddressResponse.state( address.getState() );
        orderDeliveryAddressResponse.pincode( address.getPincode() );
        orderDeliveryAddressResponse.landmark( address.getLandmark() );
        orderDeliveryAddressResponse.latitude( address.getLatitude() );
        orderDeliveryAddressResponse.longitude( address.getLongitude() );
        orderDeliveryAddressResponse.addressType( address.getAddressType() );

        return orderDeliveryAddressResponse.build();
    }
}
