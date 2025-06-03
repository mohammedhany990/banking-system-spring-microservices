package com.bankingsystem.transaction.client;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.bankingsystem.transaction.dto.BankAccountDto;
import com.bankingsystem.transaction.dto.UpdateBalanceRequest;
import com.bankingsystem.transaction.helper.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
@Slf4j
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

    
    public ApiResponse<BankAccountDto> updateAccountBalance(Long accountId, BigDecimal newBalance) {
        try {
            if (newBalance == null || newBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("New balance must be a non-negative value");
            }

            UpdateBalanceRequest updateRequest = new UpdateBalanceRequest(newBalance);

            return webClient.put()
                    .uri(accountServiceUrl + "/" + accountId + "/balance")
                    .bodyValue(updateRequest)
                    .retrieve()
                    .onStatus(status -> status.isError(),
                            clientResponse -> clientResponse
                                    .bodyToMono(String.class)
                                    .defaultIfEmpty("No error body")
                                    .flatMap(errorBody -> {
                                        log.error("Update balance call failed with status {} and body {}",
                                                clientResponse.statusCode(), errorBody);
                                        return Mono.error(new RuntimeException(
                                                "Failed to update account balance: " + clientResponse.statusCode()));
                                    }))
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<BankAccountDto>>() {
                    })
                    .block();

        } catch (WebClientResponseException e) {
            log.error("HTTP error when calling account service: status={}, body={}", e.getStatusCode(),
                    e.getResponseBodyAsString(), e);
            throw new RuntimeException("Failed to update account balance: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error when updating account balance", e);
            throw new RuntimeException("Failed to update account balance", e);
        }
    }

}
