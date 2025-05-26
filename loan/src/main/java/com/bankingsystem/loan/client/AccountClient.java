package com.bankingsystem.loan.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.bankingsystem.loan.dto.BankAccountDto;
import com.bankingsystem.loan.helper.ApiResponse;

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

}
