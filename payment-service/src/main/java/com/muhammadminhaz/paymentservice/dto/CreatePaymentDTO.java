package com.muhammadminhaz.paymentservice.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentDTO {
    private String orderId;
    private BigDecimal amount;
    private String currency = "usd";
    private String customerEmail;
}
