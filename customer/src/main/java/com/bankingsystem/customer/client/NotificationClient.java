package com.bankingsystem.customer.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.bankingsystem.customer.dto.CreateNotificationDto;
import com.bankingsystem.customer.dto.CustomerDto;
import com.bankingsystem.customer.dto.NotificationDto;
import com.bankingsystem.customer.helper.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationClient {
    private final RestTemplate restTemplate;

    private final String NOTIFICATION_SERVICE_URL = "http://localhost:8086/api/v1/notifications";

    public void sendNotification(CreateNotificationDto dto) {
        try {
            HttpEntity<CreateNotificationDto> requestEntity = new HttpEntity<>(dto);

            restTemplate.exchange(
                    NOTIFICATION_SERVICE_URL,
                    HttpMethod.POST,
                    requestEntity,
                    Void.class);

            log.info("Notification sent successfully.");

        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Notification service endpoint not found.");
        } catch (Exception e) {
            log.error("Failed to call notification service", e);
            throw new RuntimeException("Failed to call notification service", e);
        }
    }

}
