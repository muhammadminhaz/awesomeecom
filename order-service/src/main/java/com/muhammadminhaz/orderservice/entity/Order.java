package com.muhammadminhaz.orderservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders",
        indexes = @Index(name = "idx_order_customer_id", columnList = "customerId")
)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID cartId;

    private UUID customerId;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private BigDecimal totalPrice;

    private String status;

    private String paymentStatus;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;

    public static Order create(UUID customerId, UUID cartId, List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        Order order = new Order();
        order.customerId = customerId;
        order.status = "CREATED";
        order.paymentStatus = "UNPAID";
        order.setOrderItems(items);
        order.computeTotalPrice();
        order.cartId = cartId;
        return order;
    }

    private void setOrderItems(List<OrderItem> items) {
        this.orderItems = items;
        if (items != null) {
            items.forEach(item -> item.setOrder(this));
        }
    }

    private void computeTotalPrice() {
        if (orderItems != null) {
            this.totalPrice = orderItems.stream()
                    .map(OrderItem::getSubTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            this.totalPrice = BigDecimal.ZERO;
        }
    }

    // Controlled method to mark order as paid
    public void markAsPaid() {
        if (!"CREATED".equals(this.status)) throw new IllegalStateException("Cannot mark order as paid");
        this.status = "PAID";
    }
}
