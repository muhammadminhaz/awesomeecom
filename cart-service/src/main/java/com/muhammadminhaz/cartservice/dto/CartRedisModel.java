package com.muhammadminhaz.cartservice.dto;

import com.muhammadminhaz.cartservice.entity.Cart;
import com.muhammadminhaz.cartservice.entity.CartItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartRedisModel implements Serializable {

    private UUID cartId;
    private UUID customerId;

    private List<CartItemRedisModel> items = new ArrayList<>();

    private BigDecimal totalPrice;
    private String status;

    private LocalDateTime lastUpdatedAt;

    public Cart toCartEntity() {

        Cart cart = new Cart(
                cartId,
                customerId,
                new ArrayList<>(),
                totalPrice,
                status
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
