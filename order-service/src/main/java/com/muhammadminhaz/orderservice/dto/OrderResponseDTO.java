package com.muhammadminhaz.orderservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {
    private String orderId;
    private String status;
    private BigDecimal totalPrice;
    private List<OrderItemDTO> items;

}
