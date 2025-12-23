package com.muhammadminhaz.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {
    private String paymentIntentId;
    private String clientSecret;
    private String status;
    private UUID orderId;
    private BigDecimal amount;
    private String currency;
}
