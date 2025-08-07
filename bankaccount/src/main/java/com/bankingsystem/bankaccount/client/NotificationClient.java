package com.bankingsystem.bankaccount.client;

import com.bankingsystem.bankaccount.dto.CreateNotificationDto;
import com.bankingsystem.bankaccount.helper.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service", fallback = NotificationClientFallback.class)
public interface NotificationClient {


    @PostMapping("/api/v1/notifications")
    ApiResponse<Void> createNotification(@RequestBody CreateNotificationDto dto);

}
