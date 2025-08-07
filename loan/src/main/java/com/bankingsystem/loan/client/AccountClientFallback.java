package com.bankingsystem.loan.client;

import com.bankingsystem.loan.dto.BankAccountDto;
import com.bankingsystem.loan.helper.ApiResponse;
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
}

