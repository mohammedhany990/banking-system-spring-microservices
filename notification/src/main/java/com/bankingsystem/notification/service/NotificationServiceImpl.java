package com.bankingsystem.notification.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bankingsystem.notification.dto.CreateNotificationDto;
import com.bankingsystem.notification.dto.NotificationDto;
import com.bankingsystem.notification.entity.Notification;
import com.bankingsystem.notification.entity.NotificationType;
import com.bankingsystem.notification.exception.InvalidNotificationException;
import com.bankingsystem.notification.helper.NotificationMapper;
import com.bankingsystem.notification.repository.NotificationRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepo notificationRepo;
    private final NotificationMapper notificationMapper;
    private final NotificationPublisher notificationPublisher;

    @Override
    public NotificationDto createNotification(CreateNotificationDto dto) {
        Notification notification = notificationMapper.toEntity(dto);

        if (notification == null) {
            throw new InvalidNotificationException("Notification data could not be mapped from DTO.");
        }

         NotificationType notificationType;
        try {
            notificationType = NotificationType.valueOf(dto.getType().toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid notification type: {}", dto.getType());
            throw new RuntimeException("Invalid notification type: " + dto.getType(), e);
        }
        notification.setType(notificationType);

       
        validateNotification(notification);

        notification.setRead(false);

        Notification savedNotification = notificationRepo.save(notification);

        NotificationDto notificationDto = notificationMapper.toDto(savedNotification);

        notificationPublisher.sendNotification(notificationDto);

        return notificationDto;
    }

    private void validateNotification(Notification notification) {
        if (notification.getCustomerId() == null) {
            throw new InvalidNotificationException("Customer ID must not be null");
        }
        if (!StringUtils.hasText(notification.getTitle())) {
            throw new InvalidNotificationException("Notification title must not be null or empty");
        }
        if (!StringUtils.hasText(notification.getMessage())) {
            throw new InvalidNotificationException("Notification message must not be null or empty");
        }
        if (notification.getType() == null) {
            throw new InvalidNotificationException("Notification type must not be null");
        }
    }

    @Override
    public List<NotificationDto> getNotificationsByCustomerId(Long customerId) {
        if (customerId == null) {
            throw new InvalidNotificationException("Customer ID must not be null");
        }

        List<Notification> notifications = notificationRepo.findByCustomerId(customerId);
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    public NotificationDto markAsRead(UUID notificationId, boolean isRead) {
        if (notificationId == null) {
            throw new InvalidNotificationException("Notification ID must not be null");
        }

        Notification notification = notificationRepo.findById(notificationId)
                .orElseThrow(() -> new InvalidNotificationException("Notification not found"));

        notification.setRead(isRead);
        Notification updatedNotification = notificationRepo.save(notification);
        return notificationMapper.toDto(updatedNotification);
    }
}
