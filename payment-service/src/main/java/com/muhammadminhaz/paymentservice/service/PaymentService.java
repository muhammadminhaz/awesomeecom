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

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    public PaymentResponseDTO createPaymentIntent(CreatePaymentDTO request) {
        try {

            long amountInCents = request.getAmount().multiply(new BigDecimal("100")).longValue();

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency(request.getCurrency())
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build()
                    )
                    .putMetadata("orderId", request.getOrderId().toString())
                    .setDescription("Order #" + request.getOrderId())
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);

            PaymentResponseDTO response = new PaymentResponseDTO();
            response.setPaymentIntentId(intent.getId());
            response.setClientSecret(intent.getClientSecret());
            response.setStatus(intent.getStatus());
            response.setOrderId(request.getOrderId());
            response.setAmount(intent.getAmount());
            response.setCurrency(intent.getCurrency());

            log.info("Created payment intent {} for order {}", intent.getId(), request.getOrderId());
            return response;

        } catch (StripeException e) {
            log.error("Stripe error creating payment intent: {}", e.getMessage());
            throw new RuntimeException("Failed to create payment: " + e.getMessage());
        }
    }

}
