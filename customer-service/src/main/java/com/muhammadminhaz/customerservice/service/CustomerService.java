package com.muhammadminhaz.customerservice.service;

import com.muhammadminhaz.customerservice.dto.CustomerProfileResponseDTO;
import com.muhammadminhaz.customerservice.dto.LoginRequestDTO;
import com.muhammadminhaz.customerservice.dto.RegisterRequestDTO;
import com.muhammadminhaz.customerservice.entity.Customer;
import com.muhammadminhaz.customerservice.repository.CustomerRepository;
import com.muhammadminhaz.customerservice.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public CustomerService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public Optional<String> authenticate(LoginRequestDTO loginRequestDTO) {
        return findByUsername(loginRequestDTO.getUsername())
                .filter(u -> passwordEncoder.matches(loginRequestDTO.getPassword(), u.getPassword()))
                .map(u -> jwtUtil.generateToken(u.getUsername(), u.getRole()));
    }

    public boolean validateToken(String token) {
        try {
            jwtUtil.validateToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public Optional<Customer> findByUsername(String username) {
        return customerRepository.findByUsername(username);
    }

    public boolean register(RegisterRequestDTO registerRequestDTO) {
        if (customerRepository.existsCustomerByUsername(registerRequestDTO.getUsername())) {
            return false;
        }

        Customer customer = new Customer();
        customer.setEmail(registerRequestDTO.getEmail());
        customer.setUsername(registerRequestDTO.getUsername());
        customer.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        customer.setName(registerRequestDTO.getName());
        customer.setAddress(registerRequestDTO.getAddress());
        customer.setPhone(registerRequestDTO.getPhone());

        customerRepository.save(customer);
        return true;
    }

    public CustomerProfileResponseDTO getCustomerProfile(String token) {
        String username = jwtUtil.extractUsername(token);
        CustomerProfileResponseDTO customerProfileResponseDTO = new CustomerProfileResponseDTO();
        Customer customer = findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        customerProfileResponseDTO.setUsername(customer.getUsername());
        customerProfileResponseDTO.setName(customer.getName());
        customerProfileResponseDTO.setEmail(customer.getEmail());
        customerProfileResponseDTO.setAddress(customer.getAddress());
        customerProfileResponseDTO.setPhone(customer.getPhone());
        return customerProfileResponseDTO;
    }

    public String extractToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid Authorization header"
            );
        }
        return authHeader.substring(7);
    }
}
