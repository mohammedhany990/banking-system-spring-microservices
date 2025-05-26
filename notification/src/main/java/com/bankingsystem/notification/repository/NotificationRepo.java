package com.bankingsystem.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bankingsystem.notification.entity.Notification;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, UUID> {
    List<Notification> findByCustomerId(Long customerId);
}
