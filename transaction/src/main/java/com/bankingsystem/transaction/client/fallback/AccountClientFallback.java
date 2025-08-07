package com.bankingsystem.card.client.fallback;

import com.bankingsystem.card.client.AccountClient;
import com.bankingsystem.card.dto.BankAccountDto;
import com.bankingsystem.card.helper.ApiResponse;
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
