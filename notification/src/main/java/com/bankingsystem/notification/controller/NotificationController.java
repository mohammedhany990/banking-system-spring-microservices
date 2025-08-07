package com.bankingsystem.notification.controller;
import java.util.List;
import java.util.UUID;

import com.bankingsystem.notification.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.bankingsystem.notification.dto.CreateNotificationDto;
import com.bankingsystem.notification.dto.NotificationDto;
import com.bankingsystem.notification.helper.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createNotification(
            @RequestBody CreateNotificationDto dto) {
        notificationService.createNotification(dto);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Notification created successfully")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getNotificationsByCustomerId(
            @PathVariable Long customerId) {
        List<NotificationDto> notifications = notificationService.getNotificationsByCustomerId(customerId);
        ApiResponse<List<NotificationDto>> response = ApiResponse.<List<NotificationDto>>builder()
                .success(true)
                .message("Notifications fetched successfully")
                .data(notifications)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<NotificationDto>> markAsRead(
            @PathVariable UUID notificationId,
            @RequestParam boolean isRead) {
        NotificationDto updatedNotification = notificationService.markAsRead(notificationId, isRead);
        ApiResponse<NotificationDto> response = ApiResponse.<NotificationDto>builder()
                .success(true)
                .message("Notification status updated successfully")
                .data(updatedNotification)
                .build();
        return ResponseEntity.ok(response);
    }

}
