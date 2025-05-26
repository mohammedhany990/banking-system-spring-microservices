package com.bankingsystem.card.client;

import java.math.BigDecimal;

import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.bankingsystem.card.dto.TransactionResponse;
import com.bankingsystem.card.dto.transactions.DepositRequest;
import com.bankingsystem.card.dto.transactions.TransferRequest;
import com.bankingsystem.card.dto.transactions.WithdrawRequest;
import com.bankingsystem.card.exception.TransactionClientException;
import com.bankingsystem.card.helper.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class TransactionClient {

    private final RestTemplate restTemplate;

    private static final String TRANSACTION_SERVICE_URL = "http://localhost:8083/api/v1/transactions";

    public ApiResponse<TransactionResponse> deposit(DepositRequest depositRequest) {
        try {

            HttpEntity<DepositRequest> requestEntity = new HttpEntity<>(depositRequest);

            ResponseEntity<ApiResponse<TransactionResponse>> response = restTemplate.exchange(
                    TRANSACTION_SERVICE_URL + "/deposit",
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<ApiResponse<TransactionResponse>>() {
                    });

            return response.getBody();
        } catch (Exception e) {
            log.error("Deposit failed for accountId={}, amount={}", depositRequest.getAccountId(),
                    depositRequest.getAmount(), e);
            throw new TransactionClientException("Failed to deposit amount: " + e.getMessage());
        }
    }

    public ApiResponse<TransactionResponse> withdraw(WithdrawRequest withdrawRequest) {
        try {

            HttpEntity<WithdrawRequest> requestEntity = new HttpEntity<>(withdrawRequest);

            ResponseEntity<ApiResponse<TransactionResponse>> response = restTemplate.exchange(
                    TRANSACTION_SERVICE_URL + "/withdraw",
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<ApiResponse<TransactionResponse>>() {
                    });

            return response.getBody();
        } catch (Exception e) {
            log.error("Withdraw failed for accountId={}, amount={}", withdrawRequest.getAccountId(),
                    withdrawRequest.getAccountId(), e);
            throw new TransactionClientException("Failed to withdraw amount: " + e.getMessage());
        }
    }

    public ApiResponse<TransactionResponse> transfer(TransferRequest transferRequest) {
        try {

            HttpEntity<TransferRequest> requestEntity = new HttpEntity<>(transferRequest);

            ResponseEntity<ApiResponse<TransactionResponse>> response = restTemplate.exchange(
                    TRANSACTION_SERVICE_URL + "/transfer",
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<ApiResponse<TransactionResponse>>() {
                    });

            return response.getBody();
        } catch (Exception e) {
            log.error("Transfer failed from accountId={} to accountId={} amount={}", transferRequest.getFromAccountId(),
                    transferRequest.getToAccountId(), transferRequest.getAmount(),
                    e);
            throw new TransactionClientException("Failed to transfer amount: " + e.getMessage());
        }
    }
}
