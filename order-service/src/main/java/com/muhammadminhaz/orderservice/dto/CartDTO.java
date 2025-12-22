package com.muhammadminhaz.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    private String cartId;
    private String customerId;
    private List<CartItemDTO> items;
    private int totalItemQuantity;
    private double totalPrice;
    private String status;
}
