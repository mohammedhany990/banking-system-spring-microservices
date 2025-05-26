package com.bankingsystem.notification.service;

import java.util.List;
import java.util.UUID;

import com.bankingsystem.notification.dto.CreateNotificationDto;
import com.bankingsystem.notification.dto.NotificationDto;

public interface NotificationService {
    NotificationDto createNotification(CreateNotificationDto dto);
    List<NotificationDto> getNotificationsByCustomerId(Long customerId);
    NotificationDto markAsRead(UUID notificationId, boolean isRead);
}
