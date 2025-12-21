package com.muhammadminhaz.cartservice.kafka;

import com.muhammadminhaz.cartservice.dto.CartRedisModel;
import com.muhammadminhaz.cartservice.entity.Cart;
import com.muhammadminhaz.cartservice.repository.CartRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class CartPersistentEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(CartPersistentEventConsumer.class);

    private final CartRepository cartRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "cart-persistence-topic", groupId = "cart-service")
    @Transactional
    public void handleCartExpired(byte[] payload) {
        try {
            CartRedisModel redisCart =
                    objectMapper.readValue(payload, CartRedisModel.class);

            log.info("KAFKA → Persisting cart {}", redisCart.getCartId());

            Cart cart = cartRepository
                    .findById(redisCart.getCartId())
                    .orElseGet(redisCart::toCartEntity);

            // UPSERT — Redis is source of truth
            cart.updateFromRedis(redisCart);
            cartRepository.save(cart);
            log.info("KAFKA → Data Saved To DB successfully {}", redisCart.getCartId());
            // Clean Redis after successful persistence
            redisTemplate.delete("cart:" + redisCart.getCustomerId());
            log.info("Cart deleted from Redis {}", redisCart.getCartId());
        } catch (Exception e) {
            log.error("KAFKA → Failed to persist cart", e);
            throw e; // retry by Kafka
        }
    }
}
