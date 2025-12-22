package com.muhammadminhaz.orderservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID productId;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subTotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    public static OrderItem fromDTO(String productId, int quantity, BigDecimal price, BigDecimal subTotal) {
        OrderItem item = new OrderItem();
        item.productId = UUID.fromString(productId);
        item.quantity = quantity;
        item.price = price;
        item.subTotal = subTotal != null ? subTotal : price.multiply(BigDecimal.valueOf(quantity));
        return item;
    }

    void setOrder(Order order) {
        this.order = order; // package-private to allow only Order entity to set it
    }
}
