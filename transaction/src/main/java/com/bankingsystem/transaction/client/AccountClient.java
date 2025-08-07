package com.bankingsystem.transaction.client;


import com.bankingsystem.transaction.client.fallback.AccountClientFallback;
import com.bankingsystem.transaction.dto.BankAccountDto;
import com.bankingsystem.transaction.dto.UpdateBalanceRequest;
import com.bankingsystem.transaction.helper.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "bank-account-service", fallback = AccountClientFallback.class)
public interface AccountClient {

    @GetMapping("/api/v1/bank-accounts/{id}")
    ApiResponse<BankAccountDto> getAccountById(@PathVariable Long id);

    @PutMapping("/api/v1/bank-accounts/{id}/balance")
    ApiResponse<BankAccountDto> updateBalance(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBalanceRequest request);
}