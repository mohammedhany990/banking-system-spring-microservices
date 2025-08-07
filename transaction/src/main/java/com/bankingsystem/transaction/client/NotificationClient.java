package com.bankingsystem.transaction.client;



import com.bankingsystem.transaction.client.fallback.NotificationClientFallback;
import com.bankingsystem.transaction.dto.CreateNotificationDto;
import com.bankingsystem.transaction.helper.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service", fallback = NotificationClientFallback.class)
public interface NotificationClient {


    @PostMapping("/api/v1/notifications")
    ApiResponse<Void> createNotification(@RequestBody CreateNotificationDto dto);
}