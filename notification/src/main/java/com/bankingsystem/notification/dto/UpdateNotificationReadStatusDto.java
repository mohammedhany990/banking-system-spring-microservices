package com.bankingsystem.notification.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNotificationReadStatusDto {

    @NotNull(message = "Notification ID must not be null")
    private UUID notificationId;

    private boolean isRead;
}
