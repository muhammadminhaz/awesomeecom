package com.muhammadminhaz.orderservice.controller;

import com.muhammadminhaz.orderservice.dto.CreateOrderRequestDTO;
import com.muhammadminhaz.orderservice.dto.OrderResponseDTO;
import com.muhammadminhaz.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create-order")
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody CreateOrderRequestDTO request) {
        try {
            OrderResponseDTO response = orderService.createOrder(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable String orderId) {
        OrderResponseDTO response = orderService.getOrderById(orderId);
        return ResponseEntity.ok(response);
    }

//    @GetMapping("/customer/{customerId}")
//    public ResponseEntity<List<OrderResponseDTO>> getOrdersByCustomer(@PathVariable String customerId,
//                                                                      @RequestParam(required = false) String status,
//                                                                      @RequestParam(defaultValue = "10") int limit) {
//        List<OrderResponseDTO> orders = orderService.getOrdersByCustomer(customerId, status, limit);
//        return ResponseEntity.ok(orders);
//    }
//
//    // 4️⃣ Update order status (internal)
//    @PutMapping("/{orderId}/status")
//    public ResponseEntity<OrderResponseDTO> updateOrderStatus(@PathVariable UUID orderId,
//                                                              @RequestBody UpdateOrderStatusRequestDTO request) {
//        OrderResponseDTO response = orderService.updateOrderStatus(orderId, request);
//        return ResponseEntity.ok(response);
//    }
//
//    // 5️⃣ Cancel order
//    @PostMapping("/{orderId}/cancel")
//    public ResponseEntity<OrderResponseDTO> cancelOrder(@PathVariable UUID orderId) {
//        OrderResponseDTO response = orderService.cancelOrder(orderId);
//        return ResponseEntity.ok(response);
//    }
//
//    // 6️⃣ Initiate payment
//    @PostMapping("/{orderId}/pay")
//    public ResponseEntity<PaymentResponseDTO> initiatePayment(@PathVariable UUID orderId,
//                                                              @RequestBody PaymentRequestDTO request) {
//        PaymentResponseDTO response = orderService.initiatePayment(orderId, request);
//        return ResponseEntity.ok(response);
//    }

}
