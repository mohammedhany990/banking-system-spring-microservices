package com.bankingsystem.notification.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.bankingsystem.notification.config.NotificationConfig;
import com.bankingsystem.notification.dto.NotificationDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final NotificationConfig notificationConfig;

    
    public void sendNotification(NotificationDto notificationDto) {
        rabbitTemplate.convertAndSend(
                notificationConfig.getInternalExchange(),
                notificationConfig.getInternalNotificationRoutingKey(),
                notificationDto);
    }
}
