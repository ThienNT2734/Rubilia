package com.rubilia.exercise201.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rubilia.exercise201.entity.Customer;
import com.rubilia.exercise201.repository.CustomerRepository;
import com.rubilia.exercise201.service.CustomerService;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public Optional<Customer> findById(UUID id) {
        return customerRepository.findById(id);
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    @Override
    public Customer save(Customer customer) {
        // Kiểm tra passwordHash không phải null trước khi mã hóa
        if (customer.getPasswordHash() == null || customer.getPasswordHash().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }
        String encodePassword = passwordEncoder.encode(customer.getPasswordHash());
        customer.setPasswordHash(encodePassword);
        return customerRepository.save(customer);
    }

    @Override
    public Customer update(UUID id, Customer updatedCustomer) {
        Customer existingCustomer = customerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + id));

        // Cập nhật các trường cho phép
        existingCustomer.setFirstName(updatedCustomer.getFirstName());
        existingCustomer.setLastName(updatedCustomer.getLastName());
        existingCustomer.setEmail(updatedCustomer.getEmail());
        existingCustomer.setUserName(updatedCustomer.getUserName());
        // Kiểm tra và cập nhật phoneNumber, address nếu không null
        if (updatedCustomer.getPhoneNumber() != null) {
            existingCustomer.setPhoneNumber(updatedCustomer.getPhoneNumber());
        }
        if (updatedCustomer.getAddress() != null) {
            existingCustomer.setAddress(updatedCustomer.getAddress());
        }
        existingCustomer.setUpdatedAt(new java.util.Date()); // Cập nhật thời gian sửa đổi

        // Nếu có mật khẩu mới thì mã hóa
        if (updatedCustomer.getPasswordHash() != null && !updatedCustomer.getPasswordHash().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(updatedCustomer.getPasswordHash());
            existingCustomer.setPasswordHash(encodedPassword);
        }

        return customerRepository.save(existingCustomer);
    }

    @Override
    public Customer updateWithoutPasswordHash(UUID id, Customer updatedCustomer) {
        Customer existingCustomer = customerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + id));

        // Cập nhật các trường cho phép, không chạm đến passwordHash
        existingCustomer.setFirstName(updatedCustomer.getFirstName());
        existingCustomer.setLastName(updatedCustomer.getLastName());
        existingCustomer.setEmail(updatedCustomer.getEmail());
        existingCustomer.setUserName(updatedCustomer.getUserName());
        if (updatedCustomer.getPhoneNumber() != null) {
            existingCustomer.setPhoneNumber(updatedCustomer.getPhoneNumber());
        }
        if (updatedCustomer.getAddress() != null) {
            existingCustomer.setAddress(updatedCustomer.getAddress());
        }
        existingCustomer.setUpdatedAt(new java.util.Date()); // Cập nhật thời gian sửa đổi

        return customerRepository.save(existingCustomer);
    }

    @Override
    public void deleteById(UUID id) {
        customerRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return customerRepository.existsById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return customerRepository.existsByEmail(email);
    }
}