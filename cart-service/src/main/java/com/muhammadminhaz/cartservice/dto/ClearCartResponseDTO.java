package com.muhammadminhaz.cartservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClearCartResponseDTO {
    private String message;
    private BigDecimal totalAmount;
    private String status;
}
