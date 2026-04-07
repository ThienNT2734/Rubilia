package com.rubilia.exercise201.repository;

import com.rubilia.exercise201.entity.CustomerNotification;
import com.rubilia.exercise201.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CustomerNotificationRepository extends JpaRepository<CustomerNotification, UUID> {

    List<CustomerNotification> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);

    List<CustomerNotification> findByCustomerIdAndIsSeenFalseOrderByCreatedAtDesc(UUID customerId);

    Long countByCustomerIdAndIsSeenFalse(UUID customerId);

    @Query("SELECT n FROM CustomerNotification n WHERE n.expiryDate IS NULL OR n.expiryDate > CURRENT_TIMESTAMP")
    List<CustomerNotification> findAllActiveNotifications();
}
