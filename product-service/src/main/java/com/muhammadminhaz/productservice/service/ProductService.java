package com.muhammadminhaz.productservice.service;

import com.muhammadminhaz.productservice.dto.CreateProductRequestDto;
import com.muhammadminhaz.productservice.dto.PaginatedResponse;
import com.muhammadminhaz.productservice.dto.ProductDto;
import com.muhammadminhaz.productservice.entity.Product;
import com.muhammadminhaz.productservice.entity.ProductImage;
import com.muhammadminhaz.productservice.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Cacheable(value = "products", key = "#page + '-' + #size + '-' + #sort + '-' + #sortBy + '-' + #searchValue")
    public PaginatedResponse<ProductDto> getAllProducts(int page, int size, String sort, String sortBy, String searchValue) {
        Pageable pageable = PageRequest.of(
                page - 1,
                size,
                sort.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending()
        );

        Page<Product> productPage;
        if (searchValue == null || searchValue.isBlank()) {
            productPage = productRepository.findAll(pageable);
        } else {
            productPage = productRepository.findByNameContainingIgnoreCase(searchValue, pageable);
        }

        List<ProductDto> dtos = productPage.stream().map(product -> {
            ProductDto dto = new ProductDto();
            dto.setSku(product.getSku());
            dto.setName(product.getName());
            dto.setDescription(product.getDescription());
            dto.setCategory(product.getCategory());
            dto.setBrand(product.getBrand());
            dto.setPrice(product.getPrice());
            dto.setStockQuantity(product.getStockQuantity());
            dto.setCreatedAt(product.getCreatedAt());
            dto.setUpdatedAt(product.getUpdatedAt());
            dto.setImageUrls(
                    product.getImages().stream()
                            .map(ProductImage::getUrl)
                            .collect(Collectors.toList())
            );
            return dto;
        }).collect(Collectors.toList());

        PaginatedResponse<ProductDto> response = new PaginatedResponse<>();
        response.setContent(dtos);
        response.setCurrentPage(productPage.getNumber() + 1);
        response.setTotalPages(productPage.getTotalPages());
        response.setTotalItems(productPage.getTotalElements());
        response.setPageSize(productPage.getSize());
        log.info("Products Size: {}", dtos.size());
        return response;
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public ProductDto createProduct(CreateProductRequestDto createProductRequestDto) {
        log.info("Product creation initiated, {}", createProductRequestDto.getName());
        Product product = Product.builder()
                .sku("PROD-" + System.currentTimeMillis() % 1_000_000_000)
                .name(createProductRequestDto.getName())
                .description(createProductRequestDto.getDescription())
                .category(createProductRequestDto.getCategory())
                .brand(createProductRequestDto.getBrand())
                .price(createProductRequestDto.getPrice())
                .stockQuantity(createProductRequestDto.getStockQuantity())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully, Product Id: {}", savedProduct.getId());
        return mapToDto(savedProduct);
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public ProductDto updateProduct(UUID id, CreateProductRequestDto updateRequest) {
        log.info("Updating product with id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        product.updateProduct(updateRequest);

        Product updatedProduct = productRepository.save(product);
        log.info("Product updated successfully, Product Id: {}", updatedProduct.getId());
        return mapToDto(updatedProduct);
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public void deleteProduct(UUID id) {
        log.info("Deleting product with id: {}", id);
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
        log.info("Product deleted successfully, Product Id: {}", id);
    }

    private ProductDto mapToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setSku(product.getSku());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setCategory(product.getCategory());
        dto.setBrand(product.getBrand());
        dto.setPrice(product.getPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        dto.setImageUrls(
                product.getImages() == null
                        ? List.of()
                        : product.getImages().stream()
                        .map(ProductImage::getUrl)
                        .toList()
        );
        return dto;
    }

}
