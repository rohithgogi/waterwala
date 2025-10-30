package paymentservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import paymentservice.dto.*;
import paymentservice.service.PaymentService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        log.info("Creating payment for order: {}", request.getOrderId());
        PaymentResponse response = paymentService.createPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<PaymentResponse> verifyPayment(@Valid @RequestBody VerifyPaymentRequest request) {
        log.info("Verifying payment for Razorpay order: {}", request.getRazorpayOrderId());
        PaymentResponse response = paymentService.verifyAndConfirmPayment(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refund")
    public ResponseEntity<PaymentResponse> refundPayment(@Valid @RequestBody RefundRequest request) {
        log.info("Processing refund for payment: {}", request.getPaymentId());
        PaymentResponse response = paymentService.refundPayment(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long paymentId) {
        log.info("Fetching payment by ID: {}", paymentId);
        PaymentResponse response = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reference/{reference}")
    public ResponseEntity<PaymentResponse> getPaymentByReference(@PathVariable String reference) {
        log.info("Fetching payment by reference: {}", reference);
        PaymentResponse response = paymentService.getPaymentByReference(reference);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByOrderId(@PathVariable Long orderId) {
        log.info("Fetching payments for order: {}", orderId);
        List<PaymentResponse> responses = paymentService.getPaymentsByOrderId(orderId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByCustomerId(@PathVariable Long customerId) {
        log.info("Fetching payments for customer: {}", customerId);
        List<PaymentResponse> responses = paymentService.getPaymentsByCustomerId(customerId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/business/{businessId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByBusinessId(@PathVariable Long businessId) {
        log.info("Fetching payments for business: {}", businessId);
        List<PaymentResponse> responses = paymentService.getPaymentsByBusinessId(businessId);
        return ResponseEntity.ok(responses);
    }
}