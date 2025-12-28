package com.muhammadminhaz.customerservice.controller;

import com.muhammadminhaz.customerservice.dto.*;
import com.muhammadminhaz.customerservice.service.CustomerService;
import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/customer")
public class CustomerController {
    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);
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
        log.info("User logged in! username: {}", loginRequestDTO.getUsername());
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @Operation(summary = "Get Customer Profile")
    @GetMapping("/me")
    public ResponseEntity<CustomerProfileResponseDTO> getCustomerProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = customerService.extractToken(authHeader);
            CustomerProfileResponseDTO profile = customerService.getCustomerProfile(token);
            return ResponseEntity.ok(profile);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (JwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong");
        }
    }

    @Operation(summary = "Update Customer Profile")
    @PutMapping("/me")
    public ResponseEntity<ProfileUpdateResponseDTO> updateCustomer(@RequestHeader("Authorization") String authHeader, @RequestBody ProfileUpdateRequestDTO profileUpdateRequestDTO) {
        try {
            String token = customerService.extractToken(authHeader);
            ProfileUpdateResponseDTO profile = customerService.updateCustomerProfile(token, profileUpdateRequestDTO);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.ok(new ProfileUpdateResponseDTO("error", e.getMessage(), profileUpdateRequestDTO.getUsername()));
        }
    }
}
