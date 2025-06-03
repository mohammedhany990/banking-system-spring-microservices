package com.bankingsystem.customer.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

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

    private final WebClient webClient;

    @Value("${notification.service.url}")
    private String notificationServiceUrl;

    public void sendNotification(CreateNotificationDto dto) {
        try {

            log.info("Calling notification service to send notification: {}", dto);

            webClient.post()
                    .uri(notificationServiceUrl)
                    .bodyValue(dto)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            // HttpEntity<CreateNotificationDto> requestEntity = new HttpEntity<>(dto);

            // restTemplate.exchange(
            // NOTIFICATION_SERVICE_URL,
            // HttpMethod.POST,
            // requestEntity,
            // Void.class);

            log.info("Notification sent successfully.");

        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Notification service endpoint not found.");
        } catch (Exception e) {
            log.error("Failed to call notification service", e);
            throw new RuntimeException("Failed to call notification service", e);
        }
    }

}
