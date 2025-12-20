package com.muhammadminhaz.cartservice.service;

import com.muhammadminhaz.cartservice.dto.AddToCartRequestDTO;
import com.muhammadminhaz.cartservice.dto.AddToCartResponseDTO;
import com.muhammadminhaz.cartservice.dto.CartItemRedisModel;
import com.muhammadminhaz.cartservice.dto.CartRedisModel;
import com.muhammadminhaz.cartservice.scheduler.CartExpiryScheduler;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CartService {
    private static final Logger log = LoggerFactory.getLogger(CartService.class);
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CartExpiryScheduler cartExpiryScheduler;

    public AddToCartResponseDTO addToCart(AddToCartRequestDTO dto) {

        // Use customerId as part of the Redis key
        String key = "cart:" + dto.getCustomerId();

        // Fetch existing cart from Redis
        Object redisObject = redisTemplate.opsForValue().get(key);
        CartRedisModel cart;

        if (redisObject == null) {
            cart = new CartRedisModel();
            cart.setCartId(UUID.randomUUID());
            cart.setCustomerId(dto.getCustomerId());
            cart.setItems(new ArrayList<>());
            cart.setTotalPrice(BigDecimal.ZERO);
            cart.setStatus("ACTIVE");
        } else if (redisObject instanceof LinkedHashMap) {
            // Convert LinkedHashMap to CartRedisModel
            cart = objectMapper.convertValue(redisObject, CartRedisModel.class);
        } else {
            cart = (CartRedisModel) redisObject;
        }

        // Check if item already exists in cart
        CartItemRedisModel existingItem = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(dto.getProductId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            // Update quantity and subtotal
            existingItem.setQuantity(existingItem.getQuantity() + 1);
            existingItem.setSubTotal(existingItem.getPrice()
                    .multiply(BigDecimal.valueOf(existingItem.getQuantity())));
        } else {
            // Add new item
            CartItemRedisModel item = new CartItemRedisModel();
            item.setId(UUID.randomUUID());
            item.setProductId(dto.getProductId());
            item.setQuantity(1);
            item.setPrice(dto.getPrice());
            item.setSubTotal(dto.getPrice().multiply(BigDecimal.valueOf(1)));
            cart.getItems().add(item);
        }

        // Recalculate total price
        BigDecimal total = cart.getItems().stream()
                .map(CartItemRedisModel::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalPrice(total);
        cart.setLastUpdatedAt(LocalDateTime.now());

        redisTemplate.opsForValue().set(key, cart, 1, TimeUnit.MINUTES);
        cartExpiryScheduler.scheduleCartPersistence(dto.getCustomerId().toString(), cart, 1);

        return new AddToCartResponseDTO(
                "Added to cart successfully",
                cart.getCartId().toString(),
                cart.getItems().size(),
                cart.getTotalPrice().doubleValue(),
                cart.getStatus()
        );
    }
}