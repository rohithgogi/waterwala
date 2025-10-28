package paymentservice.service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import paymentservice.client.OrderServiceClient;
import paymentservice.dto.*;
import paymentservice.exception.PaymentNotFoundException;
import paymentservice.exception.PaymentProcessingException;
import paymentservice.model.*;
import paymentservice.repository.PaymentRepository;
import paymentservice.repository.PaymentTransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentTransactionRepository transactionRepository;
    private final OrderServiceClient orderServiceClient;

    @Value("${payment.currency:INR}")
    private String defaultCurrency;

    public PaymentResponse createPayment(CreatePaymentRequest request){
        log.info("Creating payment for ID: {}", request.getOrderId());

        OrderDetailsResponse order=orderServiceClient.getOrderById(request.getOrderId());
        if(request.getAmount().compareTo(order.getTotalAmount())!=0){
            throw new PaymentProcessingException("Payment amout doesn't match order total");
        }

        if(paymentRepository.existsByOrderIdAndStatus(request.getOrderId(), PaymentStatus.COMPLETED)){
            throw new PaymentProcessingException("Payment already completed for this order");
        }
        try{
            PaymentIntentCreateParams params= PaymentIntentCreateParams.builder()
                    .setAmount(request.getAmount().multiply(BigDecimal.valueOf(100)).longValue())
                    .setCurrency(defaultCurrency.toLowerCase())
                    .setDescription(request.getDescription() != null ? request.getDescription() : "Order #" + order.getOrderNumber())
                    .putMetadata("orderId", request.getOrderId().toString())
                    .putMetadata("customerId", order.getCustomerId().toString())
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build()
                    )
                    .build();
            PaymentIntent intent = PaymentIntent.create(params);

            Payment payment=Payment.builder()
                    .paymentReference(generatePaymentReference())
                    .orderId(request.getOrderId())
                    .customerId(order.getCustomerId())
                    .businessId(order.getBusinessId())
                    .amount(request.getAmount())
                    .currency(defaultCurrency)
                    .paymentMethod(request.getPaymentMethod())
                    .status(PaymentStatus.PENDING)
                    .stripePaymentIntentId(intent.getId())
                    .description(request.getDescription())
                    .customerEmail(request.getCustomerEmail())
                    .customerPhone(request.getCustomerPhone())
                    .build();

            payment=paymentRepository.save(payment);

            createTransaction(payment, TransactionType.PAYMENT,request.getAmount(),
                    TransactionStatus.INITIATED,intent.getId(),"Payment Initialized");
            log.info("Payment created successfully with reference: {}", payment.getPaymentReference());

            return mapToResponse(payment, intent.getClientSecret());

        }catch (StripeException e){
            log.error("Stripe error while creating payment: {}", e.getMessage(), e);
            throw new PaymentProcessingException("Failed to create payment: " + e.getMessage());
        }

    }

    @Transactional
    public PaymentResponse confirmPayment(String paymentIntentId) {
        log.info("Confirming payment for intent ID: {}", paymentIntentId);

        try {
            // 1. Retrieve PaymentIntent from Stripe
            PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);

            // 2. Find payment record
            Payment payment = paymentRepository.findByStripePaymentIntentId(paymentIntentId)
                    .orElseThrow(() -> new PaymentNotFoundException("Payment not found for intent: " + paymentIntentId));

            // 3. Update payment based on Stripe status
            if ("succeeded".equals(intent.getStatus())) {
                payment.setStatus(PaymentStatus.COMPLETED);
                payment.setPaidAt(LocalDateTime.now());
                payment.setStripeChargeId(intent.getLatestCharge());

                // 4. Create success transaction
                createTransaction(payment, TransactionType.PAYMENT, payment.getAmount(),
                        TransactionStatus.SUCCESS, intent.getId(), "Payment completed");

                // 5. Update order status
                updateOrderPaymentStatus(payment.getOrderId(), "CONFIRMED", "COMPLETED");

            } else if ("processing".equals(intent.getStatus())) {
                payment.setStatus(PaymentStatus.PROCESSING);

            } else if ("requires_payment_method".equals(intent.getStatus()) ||
                    "canceled".equals(intent.getStatus())) {
                payment.setStatus(PaymentStatus.FAILED);
                payment.setFailedAt(LocalDateTime.now());
                payment.setFailureReason("Payment " + intent.getStatus());

                createTransaction(payment, TransactionType.PAYMENT, payment.getAmount(),
                        TransactionStatus.FAILED, intent.getId(), "Payment failed: " + intent.getStatus());

                updateOrderPaymentStatus(payment.getOrderId(), null, "FAILED");
            }

            payment = paymentRepository.save(payment);
            log.info("Payment confirmed with status: {}", payment.getStatus());

            return mapToResponse(payment, null);

        } catch (StripeException e) {
            log.error("Stripe error while confirming payment: {}", e.getMessage(), e);
            throw new PaymentProcessingException("Failed to confirm payment: " + e.getMessage());
        }
    }

    @Transactional
    public PaymentResponse refundPayment(RefundRequest request) {
        log.info("Processing refund for payment ID: {}", request.getPaymentId());

        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));

        if (!payment.canBeRefunded()) {
            throw new PaymentProcessingException("Payment cannot be refunded");
        }

        try {
            BigDecimal refundAmount = request.getAmount() != null ?
                    request.getAmount() : payment.getAmount();

            // Create Stripe refund
            RefundCreateParams params = RefundCreateParams.builder()
                    .setPaymentIntent(payment.getStripePaymentIntentId())
                    .setAmount(refundAmount.multiply(BigDecimal.valueOf(100)).longValue())
                    .setReason(RefundCreateParams.Reason.REQUESTED_BY_CUSTOMER)
                    .build();

            Refund refund = Refund.create(params);

            // Update payment
            BigDecimal currentRefunded = payment.getRefundedAmount() != null ?
                    payment.getRefundedAmount() : BigDecimal.ZERO;
            BigDecimal totalRefunded = currentRefunded.add(refundAmount);

            payment.setRefundedAmount(totalRefunded);
            payment.setRefundReason(request.getReason());
            payment.setRefundedAt(LocalDateTime.now());

            if (totalRefunded.compareTo(payment.getAmount()) >= 0) {
                payment.setStatus(PaymentStatus.REFUNDED);
            } else {
                payment.setStatus(PaymentStatus.PARTIALLY_REFUNDED);
            }

            payment = paymentRepository.save(payment);

            // Create refund transaction
            createTransaction(payment, TransactionType.REFUND, refundAmount,
                    TransactionStatus.SUCCESS, refund.getId(), "Refund processed");

            log.info("Refund processed successfully for payment: {}", payment.getPaymentReference());

            return mapToResponse(payment, null);

        } catch (StripeException e) {
            log.error("Stripe error while processing refund: {}", e.getMessage(), e);
            throw new PaymentProcessingException("Failed to process refund: " + e.getMessage());
        }

    }

    public PaymentResponse getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with ID: " + paymentId));
        return mapToResponse(payment, null);
    }

    public PaymentResponse getPaymentByReference(String reference) {
        Payment payment = paymentRepository.findByPaymentReference(reference)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with reference: " + reference));
        return mapToResponse(payment, null);
    }

    public List<PaymentResponse> getPaymentsByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId).stream()
                .map(payment -> mapToResponse(payment, null))
                .collect(Collectors.toList());
    }

    public List<PaymentResponse> getPaymentsByCustomerId(Long customerId) {
        return paymentRepository.findByCustomerId(customerId).stream()
                .map(payment -> mapToResponse(payment, null))
                .collect(Collectors.toList());
    }

    public List<PaymentResponse> getPaymentsByBusinessId(Long businessId) {
        return paymentRepository.findByBusinessId(businessId).stream()
                .map(payment -> mapToResponse(payment, null))
                .collect(Collectors.toList());
    }

    private void createTransaction(Payment payment, TransactionType type,
                                   BigDecimal amount, TransactionStatus status,
                                   String stripeTransactionId, String description) {
        PaymentTransaction transaction = PaymentTransaction.builder()
                .payment(payment)
                .type(type)
                .amount(amount)
                .status(status)
                .stripeTransactionId(stripeTransactionId)
                .description(description)
                .build();
        transactionRepository.save(transaction);
    }

    private void updateOrderPaymentStatus(Long orderId, String orderStatus, String paymentStatus) {
        try {
            OrderStatusUpdateRequest request = OrderStatusUpdateRequest.builder()
                    .orderStatus(orderStatus)
                    .paymentStatus(paymentStatus)
                    .build();
            orderServiceClient.updateOrderStatus(orderId, request);
            log.info("Order {} status updated - Order: {}, Payment: {}", orderId, orderStatus, paymentStatus);
        } catch (Exception e) {
            log.error("Failed to update order status for order {}: {}", orderId, e.getMessage());
            // Don't throw exception - payment is still successful
        }
    }

    private String generatePaymentReference() {
        return "PAY-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private PaymentResponse mapToResponse(Payment payment, String clientSecret) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .paymentReference(payment.getPaymentReference())
                .orderId(payment.getOrderId())
                .customerId(payment.getCustomerId())
                .businessId(payment.getBusinessId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .stripePaymentIntentId(payment.getStripePaymentIntentId())
                .stripeClientSecret(clientSecret)
                .description(payment.getDescription())
                .failureReason(payment.getFailureReason())
                .paidAt(payment.getPaidAt())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
