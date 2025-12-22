package com.muhammadminhaz.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO {
    private String productId;
    private BigDecimal price;
    private int quantity;
    private BigDecimal subTotal;
}
