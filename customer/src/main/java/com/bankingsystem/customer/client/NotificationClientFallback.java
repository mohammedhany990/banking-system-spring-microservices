package com.bankingsystem.customer.client;

import com.bankingsystem.customer.dto.CreateNotificationDto;
import com.bankingsystem.customer.helper.ApiResponse;
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
