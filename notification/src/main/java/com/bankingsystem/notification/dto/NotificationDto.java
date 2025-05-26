package com.bankingsystem.notification.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.bankingsystem.notification.entity.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private UUID id;
    private String title;
    private String message;
    private NotificationType type;
    private boolean isRead;
    private LocalDateTime createdAt;

}
