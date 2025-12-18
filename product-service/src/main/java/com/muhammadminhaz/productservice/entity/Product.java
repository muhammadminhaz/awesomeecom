package com.muhammadminhaz.productservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "products",
        indexes = @Index(name = "idx_product_sku", columnList = "sku", unique = true)
)
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String sku;
    private String name;
    private String description;
    private String category;
    private String brand;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Positive
    private int stockQuantity;

    @Column(precision = 10, scale = 2, nullable = false)
    @Positive
    private BigDecimal price;

    @OneToMany(
            mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ProductImage> images = new ArrayList<>();
}

