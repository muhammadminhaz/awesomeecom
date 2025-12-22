package com.muhammadminhaz.cartservice.controller;

import com.muhammadminhaz.cartservice.dto.AddToCartRequestDTO;
import com.muhammadminhaz.cartservice.dto.AddToCartResponseDTO;
import com.muhammadminhaz.cartservice.dto.ClearCartResponseDTO;
import com.muhammadminhaz.cartservice.dto.GetCartResponseDTO;
import com.muhammadminhaz.cartservice.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(("/cart"))
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

    @GetMapping("/{customerId}")
    public ResponseEntity<GetCartResponseDTO> getCart(@PathVariable String customerId) {
        try {
            GetCartResponseDTO response = cartService.getCart(customerId);
            return ResponseEntity.ok(response);
        } catch (Exception e){
            log.error("Error getting cart", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/clear-cart/{customerId}")
    public ResponseEntity<ClearCartResponseDTO> clearCart(@PathVariable String customerId) {
        try {
            ClearCartResponseDTO response = cartService.deleteCart(customerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ClearCartResponseDTO(
                    e.getMessage(),
                    null,
                    "N/A"
            ));
        }
    }
}
