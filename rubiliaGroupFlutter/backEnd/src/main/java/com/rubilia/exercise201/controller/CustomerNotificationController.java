package com.rubilia.exercise201.controller;

import com.rubilia.exercise201.entity.CustomerNotification;
import com.rubilia.exercise201.service.CustomerNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class CustomerNotificationController {

    @Autowired
    private CustomerNotificationService notificationService;

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<CustomerNotification>> getCustomerNotifications(@PathVariable UUID customerId) {
        return ResponseEntity.ok(notificationService.getNotificationsByCustomerId(customerId));
    }

    @GetMapping("/customer/{customerId}/unseen")
    public ResponseEntity<List<CustomerNotification>> getUnseenNotifications(@PathVariable UUID customerId) {
        return ResponseEntity.ok(notificationService.getUnseenNotifications(customerId));
    }

    @GetMapping("/customer/{customerId}/count-unseen")
    public ResponseEntity<Long> countUnseenNotifications(@PathVariable UUID customerId) {
        return ResponseEntity.ok(notificationService.countUnseenNotifications(customerId));
    }

    @PostMapping("/customer/{customerId}/send")
    public ResponseEntity<CustomerNotification> sendNotification(
            @PathVariable UUID customerId,
            @RequestBody Map<String, String> request) {
        String title = request.get("title");
        String content = request.get("content");
        return ResponseEntity.ok(notificationService.sendNotification(customerId, title, content));
    }

    @PostMapping("/send-promotion")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> sendBulkPromotion(@RequestBody Map<String, Object> request) {
        String title = (String) request.get("title");
        String content = (String) request.get("content");
        UUID productId = request.get("productId") != null ? UUID.fromString((String) request.get("productId")) : null;
        String imageUrl = (String) request.get("imageUrl");

        notificationService.sendBulkPromotionNotification(title, content, productId, imageUrl);
        return ResponseEntity.ok(Map.of("message", "Đang gửi thông báo khuyến mãi đến tất cả khách hàng"));
    }

    @PutMapping("/{notificationId}/seen")
    public ResponseEntity<?> markAsSeen(@PathVariable UUID notificationId) {
        notificationService.markAsSeen(notificationId);
        return ResponseEntity.ok(Map.of("message", "Đã đánh dấu đã xem"));
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<?> markAsRead(@PathVariable UUID notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(Map.of("message", "Đã đánh dấu đã đọc"));
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<?> deleteNotification(@PathVariable UUID notificationId) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok(Map.of("message", "Đã xóa thông báo"));
    }
}
