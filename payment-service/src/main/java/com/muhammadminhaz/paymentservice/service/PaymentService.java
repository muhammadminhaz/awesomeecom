package com.muhammadminhaz.paymentservice.service;

import com.muhammadminhaz.paymentservice.dto.CreatePaymentDTO;
import com.muhammadminhaz.paymentservice.dto.PaymentResponseDTO;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    public PaymentResponseDTO createPaymentIntent(CreatePaymentDTO request) {
        try {

            Long amount = request.getAmount().longValue();

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amount * 100)
                    .setCurrency(request.getCurrency())
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build()
                    )
                    .putMetadata("orderId", request.getOrderId())
                    .setDescription("Order #" + request.getOrderId())
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);

            PaymentResponseDTO response = new PaymentResponseDTO();
            response.setPaymentIntentId(intent.getId());
            response.setClientSecret(intent.getClientSecret());
            response.setStatus(intent.getStatus());
            response.setOrderId(UUID.fromString(request.getOrderId()));
            response.setAmount(BigDecimal.valueOf(intent.getAmount()));
            response.setCurrency(intent.getCurrency());

            logger.info("Created payment intent {} for order {}", intent.getId(), request.getOrderId());
            return response;

        } catch (StripeException e) {
            logger.error("Stripe error creating payment intent: {}", e.getMessage());
            throw new RuntimeException("Failed to create payment: " + e.getMessage());
        }
    }

    public void handleWebhookEvent(String eventType, String paymentIntentId) {
        logger.info("Handling webhook event: {} for payment intent: {}", eventType, paymentIntentId);

        switch (eventType) {
            case "payment_intent.succeeded":
                handlePaymentSuccess(paymentIntentId);
                break;
            case "payment_intent.payment_failed":
                handlePaymentFailed(paymentIntentId);
                break;
            default:
                logger.info("Unhandled event type: {}", eventType);
        }
    }

    private void handlePaymentSuccess(String paymentIntentId) {
        try {
            PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);
            String orderId = intent.getMetadata().get("orderId");

            //TODO Notify order service


            // Send receipt notification
            String email = intent.getReceiptEmail();
            if (email != null) {
                String amount = String.format("%.2f", intent.getAmount() / 100.0);
            }

            logger.info("Payment succeeded for order: {}", orderId);
        } catch (Exception e) {
            logger.error("Error handling payment success: {}", e.getMessage());
        }
    }

    private void handlePaymentFailed(String paymentIntentId) {
        logger.warn("Payment failed for intent: {}", paymentIntentId);
    }
}
