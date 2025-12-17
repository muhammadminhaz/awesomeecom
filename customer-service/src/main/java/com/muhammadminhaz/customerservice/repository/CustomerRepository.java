package com.muhammadminhaz.customerservice.repository;

import com.muhammadminhaz.customerservice.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByUsername(String username);

    boolean existsCustomerByUsername(String username);
}
