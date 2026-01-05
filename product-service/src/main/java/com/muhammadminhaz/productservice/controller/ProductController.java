package com.muhammadminhaz.productservice.controller;

import com.muhammadminhaz.productservice.dto.CreateProductRequestDto;
import com.muhammadminhaz.productservice.dto.PaginatedResponse;
import com.muhammadminhaz.productservice.dto.ProductDto;
import com.muhammadminhaz.productservice.entity.Product;
import com.muhammadminhaz.productservice.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "Get paginated products")
    public ResponseEntity<PaginatedResponse<ProductDto>> getAllProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "asc") String sort,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "") String searchValue
    ) {
        PaginatedResponse<ProductDto> productPage = productService.getAllProducts(
                page,
                size,
                sort,
                sortBy,
                searchValue
        );

        return ResponseEntity.ok(productPage);
    }


    @PostMapping("/create")
    public ResponseEntity<ProductDto> createProduct(@RequestBody CreateProductRequestDto createProductRequestDto) {
        ProductDto createdProduct = productService.createProduct(createProductRequestDto);
        return ResponseEntity.ok(createdProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable UUID id, @RequestBody CreateProductRequestDto updateRequest) {
        ProductDto updatedProduct = productService.updateProduct(id, updateRequest);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }


}
