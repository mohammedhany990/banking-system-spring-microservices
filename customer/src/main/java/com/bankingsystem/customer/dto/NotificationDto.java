package com.bankingsystem.customer.dto;


import java.time.LocalDateTime;
import java.util.UUID;

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
    private Long customerId;
    private String customerEmail;
    private String title;
    private String message;
    private NotificationType type;
    private boolean  read;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
