package com.bankingsystem.loan.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.bankingsystem.loan.dto.BankAccountDto;
import com.bankingsystem.loan.helper.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class AccountClient {

    private final WebClient webClient;

    @Value("${account.service.url}")
    private String accountServiceUrl;

    public ApiResponse<BankAccountDto> getAccountById(Long accountId) {
        try {
            return webClient.get()
                    .uri(accountServiceUrl + "/" + accountId)
                    .retrieve()
                    .onStatus(status -> status.isError(),
                            clientResponse -> clientResponse
                                    .bodyToMono(String.class)
                                    .defaultIfEmpty("No error body")
                                    .flatMap(errorBody -> {
                                        log.error("Account service call failed with status {} and body {}",
                                                clientResponse.statusCode(), errorBody);
                                        return Mono.error(new RuntimeException(
                                                "Failed to call account service: " + clientResponse.statusCode()));
                                    }))
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<BankAccountDto>>() {
                    })
                    .block();

        } catch (WebClientResponseException e) {
            log.error("HTTP error when calling account service: status={}, body={}", e.getStatusCode(),
                    e.getResponseBodyAsString(), e);
            throw new RuntimeException("Failed to call account service: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error when calling account service", e);
            throw new RuntimeException("Failed to call account service", e);
        }
    }
}