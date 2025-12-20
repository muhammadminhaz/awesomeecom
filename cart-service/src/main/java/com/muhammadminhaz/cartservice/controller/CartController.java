package com.muhammadminhaz.cartservice.controller;

import com.muhammadminhaz.cartservice.dto.AddToCartRequestDTO;
import com.muhammadminhaz.cartservice.dto.AddToCartResponseDTO;
import com.muhammadminhaz.cartservice.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(("/carts"))
public class CartController {

    private static final Logger log = LoggerFactory.getLogger(CartController.class);
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add-item")
    public ResponseEntity<AddToCartResponseDTO> addToCart(@RequestBody AddToCartRequestDTO addToCartRequestDTO) {
        try {
            AddToCartResponseDTO response = cartService.addToCart(addToCartRequestDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e){
            log.error("Error adding item to cart", e);
            return ResponseEntity.badRequest().build();
        }


    }

}
