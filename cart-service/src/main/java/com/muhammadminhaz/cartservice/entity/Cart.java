package com.muhammadminhaz.cartservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "cart",
        indexes = {
                @Index(name = "idx_cart_customer_status", columnList = "customerId,status")
        }
)
public class Cart {
    @Id
    private UUID id;
    private UUID customerId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems;

    private BigDecimal totalPrice;
    private String status;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void updateFromRedis(com.muhammadminhaz.cartservice.dto.CartRedisModel redis) {
        this.customerId = redis.getCustomerId();
        this.totalPrice = redis.getTotalPrice();
        this.status = redis.getStatus();
    }

    public void abandon() {
        this.status = "ABANDONED";
    }

    public void completed() {this.status = "COMPLETED";}

    public void cancelled() {this.status = "CANCELLED";}
}
