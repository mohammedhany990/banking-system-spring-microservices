package com.bankingsystem.card.client;


import com.bankingsystem.card.client.fallback.AccountClientFallback;
import com.bankingsystem.card.dto.BankAccountDto;
import com.bankingsystem.card.helper.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "bank-account-service", fallback = AccountClientFallback.class)
public interface AccountClient {

    @GetMapping("/api/v1/bank-accounts/{id}")
    ApiResponse<BankAccountDto> getAccountById(@PathVariable Long id);
}