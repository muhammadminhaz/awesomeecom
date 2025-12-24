package com.muhammadminhaz.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderConfirmationRequest {
    private String email;
    private String orderId;
    private String customerName;
    private String orderTotal;
    private String shippingAddress;
}
