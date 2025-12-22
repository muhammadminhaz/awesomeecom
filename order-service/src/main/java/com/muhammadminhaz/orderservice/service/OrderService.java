package com.muhammadminhaz.orderservice.service;

import com.muhammadminhaz.orderservice.client.CartClient;
import com.muhammadminhaz.orderservice.dto.*;
import com.muhammadminhaz.orderservice.entity.Order;
import com.muhammadminhaz.orderservice.entity.OrderItem;
import com.muhammadminhaz.orderservice.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final CartClient cartClient;

    @Transactional
    public OrderResponseDTO createOrder(CreateOrderRequestDTO request) {
        UUID customerId = UUID.fromString(request.getCustomerId());
        log.info("CustomerId: {}", customerId);
        CartDTO cart = cartClient.getCart(request.getCustomerId())
                .timeout(Duration.ofSeconds(20))
                .block();
        log.info("Cart: {}", cart);
        if (cart == null) {
            throw new IllegalStateException("Cart not found");
        }
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> OrderItem.fromDTO(
                        cartItem.getProductId().toString(),
                        cartItem.getQuantity(),
                        cartItem.getPrice(),
                        cartItem.getSubTotal()
                ))
                .toList();

        Order order = Order.create(customerId, UUID.fromString(cart.getCartId()), orderItems);
        orderRepository.save(order);
        return new OrderResponseDTO(
                order.getId().toString(),
                order.getStatus(),
                order.getTotalPrice(),
                orderItems.stream().map(i -> new OrderItemDTO(
                        i.getProductId().toString(),
                        i.getPrice(),
                        i.getQuantity(),
                        i.getSubTotal()
                )).collect(Collectors.toList())
        );
    }


    public OrderResponseDTO getOrderById(String orderId) {
        UUID id = UUID.fromString(orderId);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return new OrderResponseDTO(
                order.getId().toString(),
                order.getStatus(),
                order.getTotalPrice(),
                order.getOrderItems().stream().map(i -> new OrderItemDTO(
                        i.getProductId().toString(),
                        i.getPrice(),
                        i.getQuantity(),
                        i.getSubTotal()
                )).collect(Collectors.toList()));
    }
}
