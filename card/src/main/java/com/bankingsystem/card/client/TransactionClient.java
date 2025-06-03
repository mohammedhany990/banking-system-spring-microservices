package com.bankingsystem.card.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import com.bankingsystem.card.dto.TransactionResponse;
import com.bankingsystem.card.dto.transactions.DepositRequest;
import com.bankingsystem.card.dto.transactions.TransferRequest;
import com.bankingsystem.card.dto.transactions.WithdrawRequest;
import com.bankingsystem.card.exception.TransactionClientException;
import com.bankingsystem.card.helper.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@RequiredArgsConstructor
@Component
public class TransactionClient {

    private final WebClient webClient;

    @Value("${transaction.service.url}")
    private String transactionServiceUrl;

    public ApiResponse<TransactionResponse> deposit(DepositRequest depositRequest) {
        try {
            return webClient.post()
                    .uri(transactionServiceUrl + "/deposit")
                    .bodyValue(depositRequest)
                    .retrieve()
                    .onStatus(status -> status.isError(),
                            clientResponse -> clientResponse
                                    .bodyToMono(String.class)
                                    .defaultIfEmpty("No error body")
                                    .flatMap(errorBody -> {

                                        log.error("Deposit failed with status {} and body {}",
                                                clientResponse.statusCode(),
                                                errorBody);

                                        return Mono.error(new TransactionClientException(
                                                "Deposit failed: " + clientResponse.statusCode()));
                                    }))
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<TransactionResponse>>() {
                    })
                    .block();

        } catch (WebClientResponseException e) {
            log.error("HTTP error during deposit: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString(),
                    e);
            throw new TransactionClientException("Failed to deposit amount: " + e.getMessage());
        } catch (Exception e) {
            log.error("Deposit failed for accountId={}, amount={}", depositRequest.getAccountId(),
                    depositRequest.getAmount(), e);
            throw new TransactionClientException("Failed to deposit amount: " + e.getMessage());
        }
    }

    public ApiResponse<TransactionResponse> withdraw(WithdrawRequest withdrawRequest) {
        try {
            return webClient.post()
                    .uri(transactionServiceUrl + "/withdraw")
                    .bodyValue(withdrawRequest)
                    .retrieve()
                    .onStatus(status -> status.isError(), clientResponse -> clientResponse.bodyToMono(String.class)
                            .defaultIfEmpty("No error body")
                            .flatMap(errorBody -> {
                                log.error("Withdraw failed with status {} and body {}", clientResponse.statusCode(),
                                        errorBody);
                                return Mono.error(new TransactionClientException(
                                        "Withdraw failed: " + clientResponse.statusCode()));
                            }))
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<TransactionResponse>>() {
                    })
                    .block();
        } catch (WebClientResponseException e) {
            log.error("HTTP error during withdraw: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString(),
                    e);
            throw new TransactionClientException("Failed to withdraw amount: " + e.getMessage());

        } catch (Exception e) {
            log.error("Withdraw failed for accountId={}, amount={}", withdrawRequest.getAccountId(),
                    withdrawRequest.getAmount(), e);
            throw new TransactionClientException("Failed to withdraw amount: " + e.getMessage());
        }
    }

    public ApiResponse<TransactionResponse> transfer(TransferRequest transferRequest) {
        try {
            return webClient.post()
                    .uri(transactionServiceUrl + "/transfer")
                    .bodyValue(transferRequest)
                    .retrieve()
                    .onStatus(status -> status.isError(), clientResponse -> clientResponse.bodyToMono(String.class)
                            .defaultIfEmpty("No error body")
                            .flatMap(errorBody -> {
                                log.error("Transfer failed with status {} and body {}", clientResponse.statusCode(),
                                        errorBody);
                                return Mono.error(new TransactionClientException(
                                        "Transfer failed: " + clientResponse.statusCode()));
                            }))
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<TransactionResponse>>() {
                    })
                    .block();
                    
        } catch (WebClientResponseException e) {
            log.error("HTTP error during transfer: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString(),
                    e);
            throw new TransactionClientException("Failed to transfer amount: " + e.getMessage());
        } catch (Exception e) {
            log.error("Transfer failed from accountId={} to accountId={} amount={}", transferRequest.getFromAccountId(),
                    transferRequest.getToAccountId(), transferRequest.getAmount(), e);
            throw new TransactionClientException("Failed to transfer amount: " + e.getMessage());
        }
    }
}
