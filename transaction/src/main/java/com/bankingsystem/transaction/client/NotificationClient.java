package com.bankingsystem.card.client;


import com.bankingsystem.card.client.fallback.NotificationClientFallback;
import com.bankingsystem.card.dto.CreateNotificationDto;
import com.bankingsystem.card.helper.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service", fallback = NotificationClientFallback.class)
public interface NotificationClient {


    @PostMapping("/api/v1/notifications")
    ApiResponse<Void> createNotification(@RequestBody CreateNotificationDto dto);
}