package com.muhammadminhaz.cartservice.kafka;

import com.muhammadminhaz.cartservice.dto.CartRedisModel;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class CartPersistentEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(CartPersistentEventPublisher.class);
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public void publishCartEvent(CartRedisModel cart) {
        try {
            byte[] payload = objectMapper.writeValueAsBytes(cart);
            CompletableFuture<SendResult<String, byte[]>> future = kafkaTemplate.send(
                    "cart-persistence-topic",
                    cart.getCustomerId().toString(),
                    payload
            );

            // Add callback to verify send success
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("KAFKA ---------→ Successfully published cart event. Offset: {}",
                            result.getRecordMetadata().offset());
                } else {
                    log.error("KAFKA ---------→ Failed to publish cart event", ex);
                }
            });

        } catch (Exception e) {
            log.error("KAFKA ---------→ Failed to serialize cart event", e);
        }
    }
}

