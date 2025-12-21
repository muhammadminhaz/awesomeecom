package com.muhammadminhaz.cartservice.dto;

import com.muhammadminhaz.cartservice.entity.Cart;
import com.muhammadminhaz.cartservice.entity.CartItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartRedisModel implements Serializable {

    private UUID cartId;
    private UUID customerId;

    private List<CartItemRedisModel> items = new ArrayList<>();

    private BigDecimal totalPrice;
    private String status;

    private LocalDateTime updatedAt;

    public static CartRedisModel fromEntity(Cart dbCart) {

        CartRedisModel redisCart = new CartRedisModel();

        redisCart.setCartId(dbCart.getId());
        redisCart.setCustomerId(dbCart.getCustomerId());
        redisCart.setTotalPrice(dbCart.getTotalPrice());
        redisCart.setStatus(dbCart.getStatus());
        redisCart.setUpdatedAt(LocalDateTime.now());

        List<CartItemRedisModel> redisItems = dbCart.getCartItems().stream()
                .map(item -> {
                    CartItemRedisModel redisItem = new CartItemRedisModel();
                    redisItem.setId(item.getId()); // optional, Redis-only
                    redisItem.setProductId(item.getProductId());
                    redisItem.setPrice(item.getPrice());
                    redisItem.setQuantity(item.getQuantity());
                    redisItem.setSubTotal(item.getSubTotal());
                    return redisItem;
                })
                .toList();

        redisCart.setItems(new ArrayList<>(redisItems));

        return redisCart;
    }


    public Cart toCartEntity() {

        Cart cart = new Cart(
                cartId,
                customerId,
                new ArrayList<>(),
                totalPrice,
                status,
                updatedAt
        );

        List<CartItem> cartItems = items.stream()
                .map(i -> new CartItem(
                        null, // 🔥 IMPORTANT
                        i.getProductId(),
                        i.getPrice(),
                        i.getQuantity(),
                        i.getSubTotal(),
                        cart
                ))
                .toList();

        cart.getCartItems().addAll(cartItems);
        return cart;
    }


}
