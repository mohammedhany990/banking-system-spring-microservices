package com.bankingsystem.card.client.fallback;


import com.bankingsystem.card.dto.CreateNotificationDto;
import com.bankingsystem.card.client.NotificationClient;
import com.bankingsystem.card.helper.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationClientFallback implements NotificationClient {

    @Override
    public ApiResponse<Void> createNotification(CreateNotificationDto dto) {
        log.error("Notification service is unavailable. Fallback triggered.");
        return ApiResponse.<Void>builder()
                .success(false)
                .message("Notification service is currently unavailable")
                .build();
    }
}