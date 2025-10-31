package paymentservice.controller;

import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import paymentservice.service.PaymentService;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
@Slf4j
public class RazorpayWebhookController {

    private final PaymentService paymentService;

    @Value("${razorpay.webhook.secret}")
    private String webhookSecret;

    @PostMapping("/razorpay")
    public ResponseEntity<String> handleRazorpayWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature) {

        log.info("Received Razorpay webhook");

        try {
            // Verify webhook signature
            if (!verifyWebhookSignature(payload, signature)) {
                log.error("Invalid webhook signature");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
            }

            // Parse webhook payload
            JSONObject webhook = new JSONObject(payload);
            String event = webhook.getString("event");
            JSONObject payloadData = webhook.getJSONObject("payload");
            JSONObject paymentEntity = payloadData.getJSONObject("payment").getJSONObject("entity");

            log.info("Processing webhook event: {}", event);

            switch (event) {
                case "payment.captured":
                    handlePaymentCaptured(paymentEntity);
                    break;

                case "payment.failed":
                    handlePaymentFailed(paymentEntity);
                    break;

                case "payment.authorized":
                    handlePaymentAuthorized(paymentEntity);
                    break;

                case "refund.created":
                    handleRefundCreated(payloadData.getJSONObject("refund").getJSONObject("entity"));
                    break;

                case "refund.processed":
                    handleRefundProcessed(payloadData.getJSONObject("refund").getJSONObject("entity"));
                    break;

                default:
                    log.info("Unhandled webhook event: {}", event);
            }

            return ResponseEntity.ok("Webhook processed successfully");

        } catch (Exception e) {
            log.error("Error processing webhook: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Webhook processing failed");
        }
    }

    private boolean verifyWebhookSignature(String payload, String signature) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(webhookSecret.getBytes(), "HmacSHA256");
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
            log.error("Error verifying webhook signature: {}", e.getMessage(), e);
            return false;
        }
    }

    private void handlePaymentCaptured(JSONObject payment) {
        String paymentId = payment.getString("id");
        String orderId = payment.getString("order_id");
        log.info("Payment captured - Payment ID: {}, Order ID: {}", paymentId, orderId);

        // The payment is already verified and confirmed through the verify endpoint
        // This is just for logging and any additional processing if needed
    }

    private void handlePaymentFailed(JSONObject payment) {
        String paymentId = payment.getString("id");
        log.info("Payment failed - Payment ID: {}", paymentId);

        // Handle payment failure if needed
        // Most of the handling is done through the verify endpoint
    }

    private void handlePaymentAuthorized(JSONObject payment) {
        String paymentId = payment.getString("id");
        log.info("Payment authorized - Payment ID: {}", paymentId);
    }

    private void handleRefundCreated(JSONObject refund) {
        String refundId = refund.getString("id");
        String paymentId = refund.getString("payment_id");
        log.info("Refund created - Refund ID: {}, Payment ID: {}", refundId, paymentId);
    }

    private void handleRefundProcessed(JSONObject refund) {
        String refundId = refund.getString("id");
        String status = refund.getString("status");
        log.info("Refund processed - Refund ID: {}, Status: {}", refundId, status);
    }
}