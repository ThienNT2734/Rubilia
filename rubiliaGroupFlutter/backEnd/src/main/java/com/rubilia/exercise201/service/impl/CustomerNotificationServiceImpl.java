package com.rubilia.exercise201.service.impl;

import com.rubilia.exercise201.entity.Customer;
import com.rubilia.exercise201.entity.CustomerNotification;
import com.rubilia.exercise201.repository.CustomerNotificationRepository;
import com.rubilia.exercise201.repository.CustomerRepository;
import com.rubilia.exercise201.service.CustomerNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class CustomerNotificationServiceImpl implements CustomerNotificationService {

    @Autowired
    private CustomerNotificationRepository notificationRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public List<CustomerNotification> getNotificationsByCustomerId(UUID customerId) {
        return notificationRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    @Override
    public List<CustomerNotification> getUnseenNotifications(UUID customerId) {
        return notificationRepository.findByCustomerIdAndIsSeenFalseOrderByCreatedAtDesc(customerId);
    }

    @Override
    public Long countUnseenNotifications(UUID customerId) {
        return notificationRepository.countByCustomerIdAndIsSeenFalse(customerId);
    }

    @Override
    public CustomerNotification sendNotification(UUID customerId, String title, String content) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        CustomerNotification notification = new CustomerNotification();
        notification.setCustomer(customer);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setCreatedAt(new Date());
        notification.setIsSeen(false);
        notification.setIsRead(false);

        return notificationRepository.save(notification);
    }

    @Override
    public CustomerNotification sendPromotionNotification(UUID customerId, String title, String content, UUID productId, String imageUrl) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        CustomerNotification notification = new CustomerNotification();
        notification.setCustomer(customer);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setNotificationType("PROMOTION");
        notification.setRelatedProductId(productId);
        notification.setImageUrl(imageUrl);
        notification.setCreatedAt(new Date());
        notification.setIsSeen(false);
        notification.setIsRead(false);

        return notificationRepository.save(notification);
    }

    @Override
    @Async
    public void sendBulkPromotionNotification(String title, String content, UUID productId, String imageUrl) {
        List<Customer> allCustomers = customerRepository.findAllByActiveTrue();

        for (Customer customer : allCustomers) {
            try {
                CustomerNotification notification = new CustomerNotification();
                notification.setCustomer(customer);
                notification.setTitle(title);
                notification.setContent(content);
                notification.setNotificationType("PROMOTION");
                notification.setRelatedProductId(productId);
                notification.setImageUrl(imageUrl);
                notification.setCreatedAt(new Date());
                notification.setIsSeen(false);
                notification.setIsRead(false);

                notificationRepository.save(notification);
            } catch (Exception e) {
                // Skip failed notifications, continue with others
                e.printStackTrace();
            }
        }
    }

    @Override
    public void markAsSeen(UUID notificationId) {
        CustomerNotification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setIsSeen(true);
        notificationRepository.save(notification);
    }

    @Override
    public void markAsRead(UUID notificationId) {
        CustomerNotification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setIsRead(true);
        notification.setIsSeen(true);
        notificationRepository.save(notification);
    }

    @Override
    public void deleteNotification(UUID notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}
