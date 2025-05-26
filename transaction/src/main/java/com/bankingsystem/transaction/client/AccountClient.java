package com.bankingsystem.transaction.client;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.bankingsystem.transaction.dto.BankAccountDto;
import com.bankingsystem.transaction.dto.UpdateBalanceRequest;
import com.bankingsystem.transaction.helper.ApiResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class AccountClient {

    private final RestTemplate restTemplate;

    private final String ACCOUNT_SERVICE_URL = "http://localhost:8082/api/v1/bank-accounts";

    public ApiResponse<BankAccountDto> getAccountById(Long accountId) {
        try {
            ResponseEntity<ApiResponse<BankAccountDto>> response = restTemplate.exchange(
                    ACCOUNT_SERVICE_URL + "/" + accountId,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<ApiResponse<BankAccountDto>>() {
                    });
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to call account service", e);
        }
    }

    public ApiResponse<BankAccountDto> updateAccountBalance(Long accountId, BigDecimal newBalance) {
        try {
            if (newBalance == null || newBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("New balance must be a non-negative value");
            }

            // Create the request body
            UpdateBalanceRequest updateRequest = new UpdateBalanceRequest(newBalance);

            HttpEntity<UpdateBalanceRequest> requestEntity = new HttpEntity<>(updateRequest);

            ResponseEntity<ApiResponse<BankAccountDto>> response = restTemplate.exchange(
                    ACCOUNT_SERVICE_URL + "/" + accountId + "/balance",
                    HttpMethod.PUT, // Changed to PUT to match your controller
                    requestEntity,
                    new ParameterizedTypeReference<ApiResponse<BankAccountDto>>() {
                    });

            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to update account balance", e);
        }
    }

}

/*
 * 
 * public ApiResponse<BankAccountDto> getAccountByNumber(String accountNumber) {
 * try {
 * ResponseEntity<ApiResponse<BankAccountDto>> response = restTemplate.exchange(
 * ACCOUNT_SERVICE_URL + "/account-number/" + accountNumber,
 * HttpMethod.GET,
 * null,
 * new ParameterizedTypeReference<ApiResponse<BankAccountDto>>() {
 * });
 * return response.getBody();
 * 
 * } catch (Exception e) {
 * throw new RuntimeException("Failed to call account service", e);
 * }
 * }
 * 
 * public ApiResponse<BankAccountDto> getAccountsByCustomerId(Long customerId) {
 * try {
 * ResponseEntity<ApiResponse<BankAccountDto>> response = restTemplate.exchange(
 * ACCOUNT_SERVICE_URL + "/customer/" + customerId,
 * HttpMethod.GET,
 * null,
 * new ParameterizedTypeReference<ApiResponse<BankAccountDto>>() {
 * });
 * return response.getBody();
 * 
 * } catch (Exception e) {
 * throw new RuntimeException("Failed to call account service", e);
 * }
 * }
 * 
 * public ApiResponse<BankAccountDto> getAccountsByType(String accountType) {
 * try {
 * ResponseEntity<ApiResponse<BankAccountDto>> response = restTemplate.exchange(
 * ACCOUNT_SERVICE_URL + "/account-type/" + accountType,
 * HttpMethod.GET,
 * null,
 * new ParameterizedTypeReference<ApiResponse<BankAccountDto>>() {
 * });
 * return response.getBody();
 * } catch (Exception e) {
 * throw new RuntimeException("Failed to call account service", e);
 * }
 * }
 * 
 */
