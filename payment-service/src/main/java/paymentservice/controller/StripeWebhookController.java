package paymentservice.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import paymentservice.service.PaymentService;

@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
@Slf4j
public class StripeWebhookController {
    private final PaymentService paymentService;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
                                                      @RequestHeader("Stripe-Signature") String sigHeader){
        log.info("Received Stripe Webhook");
        Event event;
        try{
            event= Webhook.constructEvent(payload,sigHeader,webhookSecret);
        }catch(SignatureVerificationException e){
            log.error("Invalid webhook signature: {}",e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Signature");
        }
        String eventType=event.getType();
        log.info("Processing webhook event: {}",eventType);

        try {
            switch (eventType) {
                case "payment_intent.succeeded":
                    handlePaymentIntentSucceeded(event);
                    break;

                case "payment_intent.payment_failed":
                    handlePaymentIntentFailed(event);
                    break;

                case "payment_intent.canceled":
                    handlePaymentIntentCanceled(event);
                    break;

                case "charge.refunded":
                    handleChargeRefunded(event);
                    break;

                default:
                    log.info("Unhandled event type: {}", eventType);
            }
            return ResponseEntity.ok("Webhook handled successfully");
        }catch (Exception e) {
            log.error("Error processing webhook: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Webhook processing failed");
        }
    }
    private void handlePaymentIntentSucceeded(Event event) {
        PaymentIntent paymentIntent = deserializePaymentIntent(event);
        if (paymentIntent != null) {
            log.info("Payment succeeded for intent: {}", paymentIntent.getId());
            paymentService.confirmPayment(paymentIntent.getId());
        }
    }

    private void handlePaymentIntentFailed(Event event) {
        PaymentIntent paymentIntent = deserializePaymentIntent(event);
        if (paymentIntent != null) {
            log.info("Payment failed for intent: {}", paymentIntent.getId());
            paymentService.confirmPayment(paymentIntent.getId());
        }
    }

    private void handlePaymentIntentCanceled(Event event) {
        PaymentIntent paymentIntent = deserializePaymentIntent(event);
        if (paymentIntent != null) {
            log.info("Payment canceled for intent: {}", paymentIntent.getId());
            paymentService.confirmPayment(paymentIntent.getId());
        }
    }

    private void handleChargeRefunded(Event event) {
        log.info("Charge refunded event received");
        // Additional refund handling if needed
    }

    private PaymentIntent deserializePaymentIntent(Event event) {
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        if (dataObjectDeserializer.getObject().isPresent()) {
            StripeObject stripeObject = dataObjectDeserializer.getObject().get();
            if (stripeObject instanceof PaymentIntent) {
                return (PaymentIntent) stripeObject;
            }
        }
        log.warn("Failed to deserialize PaymentIntent from event");
        return null;
    }
}
