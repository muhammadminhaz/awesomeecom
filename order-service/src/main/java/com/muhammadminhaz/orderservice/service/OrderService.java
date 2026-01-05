package com.muhammadminhaz.orderservice.service;

import com.muhammadminhaz.orderservice.client.CartClient;
import com.muhammadminhaz.orderservice.client.CustomerClient;
import com.muhammadminhaz.orderservice.client.NotificationClient;
import com.muhammadminhaz.orderservice.dto.*;
import com.muhammadminhaz.orderservice.entity.Order;
import com.muhammadminhaz.orderservice.entity.OrderItem;
import com.muhammadminhaz.orderservice.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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
    private final CustomerClient customerClient;
    private final NotificationClient notificationClient;

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
        //TODO Update Cart, Add Payment Gateway
        orderRepository.save(order);

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        sendOrderNotification(order, request.getCustomerId());
                    }
                }
        );


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

    private void sendOrderNotification(Order order, String customerId) {
        try {
            CustomerResponseDTO customer =
                    customerClient.getCustomer(customerId).block();

            if (customer == null) {
                log.warn("Customer not found, skipping notification");
                return;
            }

            NotificationRequestDTO notificationRequest =
                    NotificationRequestDTO.builder()
                            .email(customer.getEmail())
                            .orderId(order.getId().toString())
                            .customerName(customer.getName())
                            .orderTotal(order.getTotalPrice().toString())
                            .shippingAddress(customer.getAddress())
                            .build();

            notificationClient.sendOrderConfirmation(notificationRequest)
                    .block();

            log.info("Order confirmation notification sent");

        } catch (Exception e) {
            log.error("Failed to send order notification", e);
        }
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
