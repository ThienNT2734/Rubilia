package com.rubilia.exercise201.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.rubilia.exercise201.entity.Customer;

public interface CustomerService {
    List<Customer> findAll();

    Optional<Customer> findById(UUID id);

    Optional<Customer> findByEmail(String email);

    Customer save(Customer customer);

    void deleteById(UUID id);

    boolean existsById(UUID id);

    Customer update(UUID id, Customer updatedCustomer);

    Customer updateWithoutPasswordHash(UUID id, Customer updatedCustomer); // Thêm phương thức mới

    boolean existsByEmail(String email);
}