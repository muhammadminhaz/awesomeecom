package com.muhammadminhaz.cartservice.kafka;


import com.muhammadminhaz.cartservice.dto.CartRedisModel;
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
            CartRedisModel cart =
                    objectMapper.readValue(payload, CartRedisModel.class);

            if (cartRepository.existsById(cart.getCartId())) {
                log.info("Cart already persisted, skipping {}", cart.getCartId());
                return;
            }
            log.info("KAFKA ---------→ Persisting cart {}", cart.getCartId());

            cartRepository.save(cart.toCartEntity());
            redisTemplate.delete("cart:" + cart.getCustomerId());

        } catch (Exception e) {
            log.error("KAFKA ---------→ Failed to process cart event", e);
            throw e;
        }
    }
}

