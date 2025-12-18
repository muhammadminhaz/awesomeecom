package com.muhammadminhaz.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductRequestDto {
    private String name;
    private String description;
    private String category;
    private String brand;
    private BigDecimal price;
    private int stockQuantity;
    private List<String> imageUrls;
}
