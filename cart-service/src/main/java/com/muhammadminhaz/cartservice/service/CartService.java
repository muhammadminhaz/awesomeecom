package com.muhammadminhaz.cartservice.service;

import com.muhammadminhaz.cartservice.dto.*;
import com.muhammadminhaz.cartservice.entity.Cart;
import com.muhammadminhaz.cartservice.repository.CartRepository;
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
    private final CartExpiryScheduler cartExpiryScheduler;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CartRepository cartRepository;

    public AddToCartResponseDTO addToCart(AddToCartRequestDTO dto) {

        String key = "cart:" + dto.getCustomerId();

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
            cart = objectMapper.convertValue(redisObject, CartRedisModel.class);
        } else {
            cart = (CartRedisModel) redisObject;
        }

        CartItemRedisModel existingItem = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(dto.getProductId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + 1);
            existingItem.setSubTotal(
                    existingItem.getPrice()
                            .multiply(BigDecimal.valueOf(existingItem.getQuantity()))
            );
        } else {
            CartItemRedisModel item = new CartItemRedisModel();
            item.setId(UUID.randomUUID());
            item.setProductId(dto.getProductId());
            item.setQuantity(1);
            item.setPrice(dto.getPrice());
            item.setSubTotal(dto.getPrice());
            cart.getItems().add(item);
        }

        BigDecimal total = cart.getItems().stream()
                .map(CartItemRedisModel::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalPrice(total);
        cart.setUpdatedAt(LocalDateTime.now());

        int totalItemQuantity = cart.getItems().stream()
                .mapToInt(CartItemRedisModel::getQuantity)
                .sum();

        redisTemplate.opsForValue().set(key, cart, 1, TimeUnit.MINUTES);
        cartExpiryScheduler.scheduleCartPersistence(
                dto.getCustomerId().toString(), cart, 1
        );

        return new AddToCartResponseDTO(
                "Added to cart successfully",
                cart.getCartId().toString(),
                cart.getItems().size(),
                totalItemQuantity,
                cart.getTotalPrice().doubleValue(),
                cart.getStatus()
        );
    }

    public GetCartResponseDTO getCart(String customerId) {

        String key = "cart:" + customerId;
        Object redisObject = redisTemplate.opsForValue().get(key);

        CartRedisModel cart;

        // 1️⃣ Redis hit → use it
        if (redisObject != null) {
            cart = (redisObject instanceof LinkedHashMap)
                    ? objectMapper.convertValue(redisObject, CartRedisModel.class)
                    : (CartRedisModel) redisObject;
        }
        // 2️⃣ Redis miss → load from DB
        else {
            Cart dbCart = cartRepository.findCartByCustomerIdAndStatus(UUID.fromString(customerId), "ACTIVE");

            if (dbCart == null) {
                return GetCartResponseDTO.empty(customerId);
            }

            // Convert DB → Redis model
            cart = CartRedisModel.fromEntity(dbCart);

            // Hydrate Redis
            redisTemplate.opsForValue().set(key, cart, 1, TimeUnit.MINUTES);
        }

        int totalItemQuantity = cart.getItems().stream()
                .mapToInt(CartItemRedisModel::getQuantity)
                .sum();

        return new GetCartResponseDTO(
                cart.getCartId().toString(),
                cart.getCustomerId().toString(),
                cart.getItems(),
                totalItemQuantity,
                cart.getTotalPrice().doubleValue(),
                cart.getStatus()
        );
    }

}
