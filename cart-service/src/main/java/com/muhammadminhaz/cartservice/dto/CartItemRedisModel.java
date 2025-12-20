package com.muhammadminhaz.cartservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRedisModel implements Serializable {
    private UUID id;
    private UUID productId;
    private BigDecimal price;
    private int quantity;
    private BigDecimal subTotal;
}

