package com.muhammadminhaz.productservice.repository;

import com.muhammadminhaz.productservice.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Override
    @EntityGraph(attributePaths = "images")
    Page<Product> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "images")
    Page<Product> findByNameContainingIgnoreCase(String searchValue, Pageable pageable);
}
