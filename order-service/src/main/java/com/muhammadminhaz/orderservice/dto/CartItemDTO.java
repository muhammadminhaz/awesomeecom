package com.muhammadminhaz.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {
    private UUID id;
    private UUID productId;
    private BigDecimal price;
    private int quantity;
    private BigDecimal subTotal;
}
