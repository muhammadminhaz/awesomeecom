package com.muhammadminhaz.cartservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
public class GetCartResponseDTO {

    private String cartId;
    private String customerId;
    private List<CartItemRedisModel> items;
    private int totalItemQuantity;
    private double totalPrice;
    private String status;

    public static GetCartResponseDTO empty(String customerId) {
        return new GetCartResponseDTO(
                null,
                customerId,
                Collections.emptyList(),
                0,
                0.0,
                "EMPTY"
        );
    }
}
