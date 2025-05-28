package com.bankingsystem.notification.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.bankingsystem.notification.dto.NotificationDto;

@Component
@AllArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final NotificationDeliveryService deliveryService;

    @RabbitListener(queues = "${rabbitmq.queues.notification}")
    public void receiveNotification(NotificationDto notificationDto) {

        log.info("Received {} notification for customer {}: {}",
                notificationDto.getType(),
                notificationDto.getCustomerId(),
                notificationDto.getMessage());

        try {
            deliveryService.sendNotification(notificationDto);
        } catch (Exception e) {
            log.error("Failed to send {} notification to customer {}: {}",
                    notificationDto.getType(),
                    notificationDto.getCustomerId(),
                    e.getMessage());
        }
    }
}