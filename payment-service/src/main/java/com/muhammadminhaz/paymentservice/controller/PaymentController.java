package com.muhammadminhaz.paymentservice.controller;

import com.muhammadminhaz.paymentservice.dto.CreatePaymentDTO;
import com.muhammadminhaz.paymentservice.dto.PaymentResponseDTO;
import com.muhammadminhaz.paymentservice.service.PaymentService;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentService paymentService;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/initiate")
    public ResponseEntity<PaymentResponseDTO> initiatePayment(@RequestBody CreatePaymentDTO request) {
        return ResponseEntity.ok(paymentService.createPaymentIntent(request));
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        try {
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);

            String eventType = event.getType();
            logger.info("Received Stripe webhook: {}", eventType);

            if (event.getDataObjectDeserializer().getObject().isPresent()) {
                Object dataObject = event.getDataObjectDeserializer().getObject().get();

                if (dataObject instanceof PaymentIntent) {
                    PaymentIntent paymentIntent = (PaymentIntent) dataObject;
                    paymentService.handleWebhookEvent(eventType, paymentIntent.getId());
                }
            }

            return ResponseEntity.ok("Webhook processed");

        } catch (Exception e) {
            logger.error("Webhook error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook error: " + e.getMessage());
        }
    }
}
