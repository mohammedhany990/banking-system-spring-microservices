package com.bankingsystem.transaction.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.bankingsystem.transaction.dto.CreateNotificationDto;
import com.bankingsystem.transaction.exception.NotificationClientException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationClient {

    private final WebClient webClient;

    @Value("${notification.service.url}")
    private String notificationServiceUrl;

    public Mono<Void> sendNotificationAsync(CreateNotificationDto dto) {

        log.info("Sending notification to notification service: {}", notificationServiceUrl);

        return webClient.post()
                .uri(notificationServiceUrl)
                .bodyValue(dto)
                .retrieve()
                .onStatus(status -> status.isError(), response -> response.bodyToMono(String.class)
                        .defaultIfEmpty("No error body")
                        .flatMap(errorBody -> {
                            log.error("Notification service error: {} - Body: {}", response.statusCode(), errorBody);
                            return Mono.error(new NotificationClientException(
                                    "Notification service error: " + response.statusCode()));
                        }))
                .bodyToMono(Void.class)
                .doOnSuccess(unused -> log.info("Notification sent successfully."))
                .doOnError(WebClientResponseException.class,
                        e -> log.error("HTTP error calling notification service: {} - {}", e.getStatusCode(),
                                e.getResponseBodyAsString(), e))
                .doOnError(e -> log.error("Unexpected error calling notification service", e));
    }

}
