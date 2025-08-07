package com.bankingsystem.transaction.client.fallback;


import com.bankingsystem.transaction.client.AccountClient;
import com.bankingsystem.transaction.dto.BankAccountDto;
import com.bankingsystem.transaction.dto.UpdateBalanceRequest;
import com.bankingsystem.transaction.helper.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AccountClientFallback implements AccountClient {

    @Override
    public ApiResponse<BankAccountDto> getAccountById(Long id) {
        log.error("Fallback triggered: Unable to fetch bank account with ID {}", id);

        return ApiResponse.<BankAccountDto>builder()
                .success(false)
                .message("Bank account service is currently unavailable.")
                .data(null)
                .build();
    }

    @Override
    public ApiResponse<BankAccountDto> updateBalance(Long id, UpdateBalanceRequest request) {
        log.error("Fallback triggered: Unable to update balance for account ID {}", id);

        return ApiResponse.<BankAccountDto>builder()
                .success(false)
                .message("Failed to update account balance. Bank account service is down.")
                .data(null)
                .build();
    }
}
