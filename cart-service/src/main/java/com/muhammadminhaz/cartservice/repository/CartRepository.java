package com.muhammadminhaz.cartservice.repository;

import com.muhammadminhaz.cartservice.entity.Cart;
import org.apache.el.stream.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {
    Cart findCartByCustomerIdAndStatus(UUID uuid, String status);
}
