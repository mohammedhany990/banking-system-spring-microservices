package com.bankingsystem.customer.repository;

import com.bankingsystem.customer.entity.Customer;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByUsername(String username);
    
}
