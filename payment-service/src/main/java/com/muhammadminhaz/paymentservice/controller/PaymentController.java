package com.muhammadminhaz.paymentservice.controller;

import com.muhammadminhaz.paymentservice.dto.CreatePaymentDTO;
import com.muhammadminhaz.paymentservice.dto.PaymentResponseDTO;
import com.muhammadminhaz.paymentservice.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/initiate")
    public ResponseEntity<PaymentResponseDTO> initiatePayment(@RequestBody CreatePaymentDTO request) {
        return ResponseEntity.ok(paymentService.createPaymentIntent(request));
    }

}
