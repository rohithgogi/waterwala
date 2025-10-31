package paymentservice.service;

import com.razorpay.Order;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Refund;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
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

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
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
    private final RazorpayClient razorpayClient;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Value("${payment.currency:INR}")
    private String defaultCurrency;

    @Transactional
    public PaymentResponse createPayment(CreatePaymentRequest request) {
        log.info("Creating payment for Order ID: {}", request.getOrderId());

        // Fetch order details
        OrderDetailsResponse order = orderServiceClient.getOrderById(request.getOrderId());

        // Validate amount
        if (request.getAmount().compareTo(order.getTotalAmount()) != 0) {
            throw new PaymentProcessingException("Payment amount doesn't match order total");
        }

        // Check if payment already exists
        if (paymentRepository.existsByOrderIdAndStatus(request.getOrderId(), PaymentStatus.COMPLETED)) {
            throw new PaymentProcessingException("Payment already completed for this order");
        }

        try {
            // Create Razorpay Order
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", request.getAmount().multiply(BigDecimal.valueOf(100)).intValue()); // Amount in paise
            orderRequest.put("currency", defaultCurrency);
            orderRequest.put("receipt", "rcpt_" + request.getOrderId());

            JSONObject notes = new JSONObject();
            notes.put("orderId", request.getOrderId().toString());
            notes.put("customerId", order.getCustomerId().toString());
            notes.put("businessId", order.getBusinessId().toString());
            orderRequest.put("notes", notes);

            Order razorpayOrder = razorpayClient.orders.create(orderRequest);
            String razorpayOrderId = razorpayOrder.get("id").toString();
            log.info("Razorpay order created: {}", razorpayOrderId);

            // Create payment record
            paymentservice.model.Payment payment = paymentservice.model.Payment.builder()
                    .paymentReference(generatePaymentReference())
                    .orderId(request.getOrderId())
                    .customerId(order.getCustomerId())
                    .businessId(order.getBusinessId())
                    .amount(request.getAmount())
                    .currency(defaultCurrency)
                    .paymentMethod(request.getPaymentMethod())
                    .status(PaymentStatus.PENDING)
                    .razorpayOrderId(razorpayOrderId)
                    .description(request.getDescription())
                    .customerEmail(request.getCustomerEmail())
                    .customerPhone(request.getCustomerPhone())
                    .build();

            payment = paymentRepository.save(payment);

            // Create transaction record
            createTransaction(payment, TransactionType.PAYMENT, request.getAmount(),
                    TransactionStatus.INITIATED, razorpayOrderId, "Payment initialized");

            log.info("Payment created successfully with reference: {}", payment.getPaymentReference());

            return mapToResponse(payment);

        } catch (RazorpayException e) {
            log.error("Razorpay error while creating payment: {}", e.getMessage(), e);
            throw new PaymentProcessingException("Failed to create payment: " + e.getMessage());
        }
    }

    @Transactional
    public PaymentResponse verifyAndConfirmPayment(VerifyPaymentRequest request) {
        log.info("Verifying payment for Razorpay Order ID: {}", request.getRazorpayOrderId());

        // Find payment by Razorpay order ID
        paymentservice.model.Payment payment = paymentRepository
                .findByRazorpayOrderId(request.getRazorpayOrderId())
                .orElseThrow(() -> new PaymentNotFoundException(
                        "Payment not found for Razorpay order: " + request.getRazorpayOrderId()));

        try {
            // Verify signature
            boolean isValidSignature = verifySignature(
                    request.getRazorpayOrderId(),
                    request.getRazorpayPaymentId(),
                    request.getRazorpaySignature()
            );

            if (!isValidSignature) {
                log.error("Invalid payment signature for order: {}", request.getRazorpayOrderId());
                payment.setStatus(PaymentStatus.FAILED);
                payment.setFailedAt(LocalDateTime.now());
                payment.setFailureReason("Invalid payment signature");
                paymentRepository.save(payment);

                createTransaction(payment, TransactionType.PAYMENT, payment.getAmount(),
                        TransactionStatus.FAILED, request.getRazorpayPaymentId(), "Signature verification failed");

                throw new PaymentProcessingException("Invalid payment signature");
            }

            // Fetch payment details from Razorpay
            Payment razorpayPayment = razorpayClient.payments.fetch(request.getRazorpayPaymentId());
            String status = razorpayPayment.get("status");

            // Update payment based on status
            payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
            payment.setRazorpaySignature(request.getRazorpaySignature());

            if ("captured".equals(status) || "authorized".equals(status)) {
                payment.setStatus(PaymentStatus.COMPLETED);
                payment.setPaidAt(LocalDateTime.now());

                createTransaction(payment, TransactionType.PAYMENT, payment.getAmount(),
                        TransactionStatus.SUCCESS, request.getRazorpayPaymentId(), "Payment completed successfully");

                // Update order status
                updateOrderPaymentStatus(payment.getOrderId(), "CONFIRMED", "COMPLETED");

                log.info("Payment completed successfully for order: {}", payment.getOrderId());

            } else if ("failed".equals(status)) {
                payment.setStatus(PaymentStatus.FAILED);
                payment.setFailedAt(LocalDateTime.now());
                payment.setFailureReason("Payment failed at gateway");

                createTransaction(payment, TransactionType.PAYMENT, payment.getAmount(),
                        TransactionStatus.FAILED, request.getRazorpayPaymentId(), "Payment failed");

                updateOrderPaymentStatus(payment.getOrderId(), null, "FAILED");
            }

            payment = paymentRepository.save(payment);
            return mapToResponse(payment);

        } catch (RazorpayException e) {
            log.error("Razorpay error while verifying payment: {}", e.getMessage(), e);
            throw new PaymentProcessingException("Failed to verify payment: " + e.getMessage());
        }
    }

    @Transactional
    public PaymentResponse refundPayment(RefundRequest request) {
        log.info("Processing refund for payment ID: {}", request.getPaymentId());

        paymentservice.model.Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));

        if (!payment.canBeRefunded()) {
            throw new PaymentProcessingException("Payment cannot be refunded");
        }

        if (payment.getRazorpayPaymentId() == null) {
            throw new PaymentProcessingException("Razorpay payment ID not found");
        }

        try {
            BigDecimal refundAmount = request.getAmount() != null ?
                    request.getAmount() : payment.getAmount();

            // Create refund in Razorpay
            JSONObject refundRequest = new JSONObject();
            refundRequest.put("amount", refundAmount.multiply(BigDecimal.valueOf(100)).intValue());
            if (request.getReason() != null) {
                JSONObject notes = new JSONObject();
                notes.put("reason", request.getReason());
                refundRequest.put("notes", notes);
            }

            Refund refund = razorpayClient.payments.refund(payment.getRazorpayPaymentId(), refundRequest);
            String refundId = refund.get("id").toString();
            log.info("Razorpay refund created: {}", refundId);

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
                    TransactionStatus.SUCCESS, refundId, "Refund processed successfully");

            log.info("Refund processed successfully for payment: {}", payment.getPaymentReference());

            return mapToResponse(payment);

        } catch (RazorpayException e) {
            log.error("Razorpay error while processing refund: {}", e.getMessage(), e);
            throw new PaymentProcessingException("Failed to process refund: " + e.getMessage());
        }
    }

    public PaymentResponse getPaymentById(Long paymentId) {
        paymentservice.model.Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with ID: " + paymentId));
        return mapToResponse(payment);
    }

    public PaymentResponse getPaymentByReference(String reference) {
        paymentservice.model.Payment payment = paymentRepository.findByPaymentReference(reference)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with reference: " + reference));
        return mapToResponse(payment);
    }

    public List<PaymentResponse> getPaymentsByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<PaymentResponse> getPaymentsByCustomerId(Long customerId) {
        return paymentRepository.findByCustomerId(customerId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<PaymentResponse> getPaymentsByBusinessId(Long businessId) {
        return paymentRepository.findByBusinessId(businessId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Helper method to verify Razorpay signature
    private boolean verifySignature(String orderId, String paymentId, String signature) {
        try {
            String payload = orderId + "|" + paymentId;
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(razorpayKeySecret.getBytes(), "HmacSHA256");
            mac.init(secretKey);
            byte[] hash = mac.doFinal(payload.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            String generatedSignature = hexString.toString();
            return generatedSignature.equals(signature);

        } catch (Exception e) {
            log.error("Error verifying signature: {}", e.getMessage(), e);
            return false;
        }
    }

    private void createTransaction(paymentservice.model.Payment payment, TransactionType type,
                                   BigDecimal amount, TransactionStatus status,
                                   String razorpayTransactionId, String description) {
        PaymentTransaction transaction = PaymentTransaction.builder()
                .payment(payment)
                .type(type)
                .amount(amount)
                .status(status)
                .stripeTransactionId(razorpayTransactionId) // Reusing column for Razorpay ID
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
        }
    }

    private String generatePaymentReference() {
        return "PAY-" + System.currentTimeMillis() + "-" +
                UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private PaymentResponse mapToResponse(paymentservice.model.Payment payment) {
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
                .razorpayOrderId(payment.getRazorpayOrderId())
                .razorpayPaymentId(payment.getRazorpayPaymentId())
                .razorpayKeyId(razorpayKeyId) // For frontend integration
                .description(payment.getDescription())
                .failureReason(payment.getFailureReason())
                .paidAt(payment.getPaidAt())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}