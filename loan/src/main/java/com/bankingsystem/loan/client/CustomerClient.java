package com.bankingsystem.loan.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.bankingsystem.loan.dto.CustomerDto;
import com.bankingsystem.loan.helper.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
@Slf4j
public class CustomerClient {

    private final WebClient webClient;

    @Value("${customer.service.url}")
    private String customerServiceUrl;

    public Mono<ApiResponse<CustomerDto>> getCustomerById(Long customerId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(customerServiceUrl + "/{id}").build(customerId))
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> {
                    log.error("Customer service error: {}", response.statusCode());
                    return response.bodyToMono(String.class)
                            .defaultIfEmpty("No error body")
                            .flatMap(body -> Mono
                                    .error(new RuntimeException("Customer service error: " + response.statusCode())));
                })
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<CustomerDto>>() {
                })
                .doOnSuccess(response -> log.info("Fetched customer with id {}", customerId))
                .doOnError(e -> log.error("Failed to fetch customer by id {}", customerId, e));
                
    }

    public ApiResponse<CustomerDto> getCustomerByUsername(String username) {
        try {
            return webClient.get()
                    .uri(customerServiceUrl + "/username/" + username)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<CustomerDto>>() {
                    })
                    .block();

        } catch (WebClientResponseException.NotFound e) {
            log.warn("Customer with username {} not found", username);
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to call customer service", e);
        }
    }
}