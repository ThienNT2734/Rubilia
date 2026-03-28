package com.rubilia.exercise201.controller;

import com.rubilia.exercise201.entity.Customer;
import com.rubilia.exercise201.entity.Order;
import com.rubilia.exercise201.entity.OrderItem;
import com.rubilia.exercise201.service.CustomerService;
import com.rubilia.exercise201.service.OrderItemService;
import com.rubilia.exercise201.service.OrderService;
import com.rubilia.exercise201.service.util.OrderIdAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrderIdAdapter orderIdAdapter;

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable String id) {
        return orderService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Order>> getOrdersByCustomer(@PathVariable String customerId) {
        return customerService.findById(UUID.fromString(customerId))
                .map(customer -> ResponseEntity.ok(orderService.findByCustomer(customer)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{statusId}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable String statusId) {
        return orderService.findById(statusId)
                .map(order -> ResponseEntity.ok(orderService.findByOrderStatus(order.getOrderStatus())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Object orderData) {
        return orderService.processCheckout(orderData);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable String id, @RequestBody Order order) {
        if (!orderService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        order.setId(id);
        return ResponseEntity.ok(orderService.save(order));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable String orderId) {
        try {
            orderService.deleteById(orderId);
            return ResponseEntity.ok("Xóa đơn hàng thành công.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Xóa đơn hàng thất bại.");
        }
    }

    @GetMapping("/{orderId}/items")
    public ResponseEntity<List<OrderItem>> getOrderItems(@PathVariable String orderId) {
        return orderService.findById(orderId)
                .map(order -> ResponseEntity.ok(orderItemService.findByOrder(order)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{orderId}/items")
    public ResponseEntity<OrderItem> addOrderItem(
            @PathVariable String orderId,
            @RequestBody OrderItem orderItem) {
        return orderService.findById(orderId)
                .map(order -> {
                    orderItem.setOrder(order);
                    return ResponseEntity.ok(orderItemService.save(orderItem));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<Void> deleteOrderItem(
            @PathVariable String orderId,
            @PathVariable String itemId) {
        if (!orderService.existsById(orderId)) {
            return ResponseEntity.notFound().build();
        }
        orderItemService.deleteById(UUID.fromString(itemId));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveOrder(
            @PathVariable("id") String orderId,
            @RequestParam("staffId") String staffId) {
        try {
            UUID staffUuid = UUID.fromString(staffId);
            Order approvedOrder;
            if (orderIdAdapter.isCustomFormat(orderId)) {
                approvedOrder = orderService.approveOrder(orderId, staffUuid);
            } else {
                UUID orderUuid = orderIdAdapter.toUUID(orderId);
                if (orderUuid == null) {
                    return ResponseEntity.badRequest().body("Invalid order ID format.");
                }
                approvedOrder = orderService.approveOrder(orderUuid, staffUuid);
            }
            return ResponseEntity.ok(approvedOrder);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{orderId}/ship")
    public ResponseEntity<?> markOrderAsShipped(
            @PathVariable String orderId,
            @RequestParam String staffId) {
        try {
            UUID staffUuid = UUID.fromString(staffId);
            Order shippedOrder;
            if (orderIdAdapter.isCustomFormat(orderId)) {
                shippedOrder = orderService.markOrderAsShipped(orderId, staffUuid);
            } else {
                UUID orderUuid = orderIdAdapter.toUUID(orderId);
                if (orderUuid == null) {
                    return ResponseEntity.badRequest().body("Invalid order ID format.");
                }
                shippedOrder = orderService.markOrderAsShipped(orderUuid, staffUuid);
            }
            return ResponseEntity.ok(shippedOrder);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{orderId}/accept")
    public ResponseEntity<?> customerAcceptOrder(@PathVariable String orderId) {
        try {
            Order acceptedOrder;
            if (orderIdAdapter.isCustomFormat(orderId)) {
                acceptedOrder = orderService.customerAcceptOrder(orderId);
            } else {
                UUID orderUuid = orderIdAdapter.toUUID(orderId);
                if (orderUuid == null) {
                    return ResponseEntity.badRequest().body("Invalid order ID format.");
                }
                acceptedOrder = orderService.customerAcceptOrder(orderUuid);
            }
            return ResponseEntity.ok(acceptedOrder);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}