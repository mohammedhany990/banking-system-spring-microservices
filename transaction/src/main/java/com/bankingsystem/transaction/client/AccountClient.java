package com.bankingsystem.transaction.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class AccountClient {

    private final RestTemplate restTemplate;

    private final String ACCOUNT_SERVICE_URL = "http://localhost:8082/api/v1/bank-accounts";

    public String getAccountById(Long accountId) {
        try {
            return restTemplate.getForObject(ACCOUNT_SERVICE_URL + "/" + accountId, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to call account service", e);
        }
    }
    public String getAccountByNumber(String accountNumber) {
        try {
            return restTemplate.getForObject(ACCOUNT_SERVICE_URL + "/account-number/" + accountNumber, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to call account service", e);
        }
    }
    public String getAccountsByCustomerId(Long customerId) {
        try {
            return restTemplate.getForObject(ACCOUNT_SERVICE_URL + "/customer/" + customerId, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to call account service", e);
        }
    }
    public String getAccountsByType(String accountType) {
        try {
            return restTemplate.getForObject(ACCOUNT_SERVICE_URL + "/account-type/" + accountType, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to call account service", e);
        }
    }

}
