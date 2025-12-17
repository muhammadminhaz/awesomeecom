package com.muhammadminhaz.customerservice.controller;

import com.muhammadminhaz.customerservice.dto.LoginRequestDTO;
import com.muhammadminhaz.customerservice.dto.LoginResponseDTO;
import com.muhammadminhaz.customerservice.dto.RegisterRequestDTO;
import com.muhammadminhaz.customerservice.dto.RegisterResponseDTO;
import com.muhammadminhaz.customerservice.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/customer")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new customer")
    public ResponseEntity<RegisterResponseDTO> register(@RequestBody RegisterRequestDTO registerRequestDTO) {
        boolean success = customerService.register(registerRequestDTO);
        if (success) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new RegisterResponseDTO("User registered successfully", registerRequestDTO.getUsername()));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new RegisterResponseDTO("Registration failed: User may already exist", "N/A"));
        }
    }

    @Operation(summary = "Generate token on user login")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        Optional<String> tokenOptional = customerService.authenticate(loginRequestDTO);
        if (tokenOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = tokenOptional.get();
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @Operation(summary = "Validate Token")
    @GetMapping("/validate")
    public ResponseEntity<Void> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return customerService.validateToken(authHeader.substring(7)) ? ResponseEntity.ok().build() : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
