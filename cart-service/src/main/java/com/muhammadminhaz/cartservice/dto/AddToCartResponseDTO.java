package com.muhammadminhaz.cartservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddToCartResponseDTO {
    private String message;
    private String cartId;
    private int totalItems;
    private double totalPrice;
    private String status;
}
