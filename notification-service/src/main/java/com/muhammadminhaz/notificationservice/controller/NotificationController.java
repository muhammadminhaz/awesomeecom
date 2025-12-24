package com.muhammadminhaz.notificationservice.controller;

import com.muhammadminhaz.notificationservice.dto.EmailResponse;
import com.muhammadminhaz.notificationservice.dto.OrderConfirmationRequest;
import com.muhammadminhaz.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notify")
@RequiredArgsConstructor
public class NotificationController {
    private final EmailService emailService;


    @PostMapping("/order-confirmation")
    public ResponseEntity<EmailResponse> sendOrderConfirmation(@RequestBody OrderConfirmationRequest request) {
        return ResponseEntity.ok(emailService.sendOrderConfirmation(request));
    }
}
