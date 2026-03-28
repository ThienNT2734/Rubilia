package com.rubilia.exercise201.service;

import com.rubilia.exercise201.entity.Customer;
import com.rubilia.exercise201.entity.Order;
import com.rubilia.exercise201.entity.OrderStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderService {
    List<Order> findAll();

    Optional<Order> findById(String id);

    List<Order> findByCustomer(Customer customer);

    List<Order> findByOrderStatus(OrderStatus orderStatus);

    List<Order> findByCustomerAndOrderStatus(Customer customer, OrderStatus orderStatus);

    Order save(Order order);

    void deleteById(String id);

    boolean existsById(String id);

    // Checkout từ dữ liệu gửi từ Checkout.js
    ResponseEntity<?> checkout(Object orderData);

    // Phương thức checkout mới
    ResponseEntity<?> processCheckout(Object orderData);

    // Các phương thức cập nhật trạng thái đơn hàng
    Order approveOrder(UUID orderId, UUID staffId);
    Order approveOrder(String orderId, UUID staffId);

    Order markOrderAsShipped(UUID orderId, UUID staffId);
    Order markOrderAsShipped(String orderId, UUID staffId);

    Order customerAcceptOrder(UUID orderId);
    Order customerAcceptOrder(String orderId);
}