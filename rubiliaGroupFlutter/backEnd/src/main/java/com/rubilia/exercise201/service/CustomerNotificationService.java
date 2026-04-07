package com.rubilia.exercise201.service;

import com.rubilia.exercise201.entity.CustomerNotification;
import java.util.List;
import java.util.UUID;

public interface CustomerNotificationService {

    List<CustomerNotification> getNotificationsByCustomerId(UUID customerId);

    List<CustomerNotification> getUnseenNotifications(UUID customerId);

    Long countUnseenNotifications(UUID customerId);

    CustomerNotification sendNotification(UUID customerId, String title, String content);

    CustomerNotification sendPromotionNotification(UUID customerId, String title, String content, UUID productId, String imageUrl);

    void sendBulkPromotionNotification(String title, String content, UUID productId, String imageUrl);

    void markAsSeen(UUID notificationId);

    void markAsRead(UUID notificationId);

    void deleteNotification(UUID notificationId);
}
