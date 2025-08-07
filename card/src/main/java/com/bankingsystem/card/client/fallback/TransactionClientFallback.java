package com.bankingsystem.card.client.fallback;

import com.bankingsystem.card.client.TransactionClient;
import com.bankingsystem.card.dto.TransactionResponse;
import com.bankingsystem.card.dto.transactions.DepositRequest;
import com.bankingsystem.card.dto.transactions.TransferRequest;
import com.bankingsystem.card.dto.transactions.WithdrawRequest;
import com.bankingsystem.card.helper.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TransactionClientFallback implements TransactionClient {

    @Override
    public ApiResponse<TransactionResponse> deposit(DepositRequest depositRequest) {
        log.error("Fallback: deposit failed for amount {}", depositRequest.getAmount());
        return failureResponse("Deposit service is unavailable");
    }

    @Override
    public ApiResponse<TransactionResponse> withdraw(WithdrawRequest withdrawRequest) {
        log.error("Fallback: withdraw failed for amount {}", withdrawRequest.getAmount());
        return failureResponse("Withdraw service is unavailable");
    }

    @Override
    public ApiResponse<TransactionResponse> transfer(TransferRequest transferRequest) {
        log.error("Fallback: transfer failed from account {} to account {} for amount {}",
                transferRequest.getFromAccountId(),
                transferRequest.getToAccountId(),
                transferRequest.getAmount());
        return failureResponse("Transfer service is unavailable");
    }

    private ApiResponse<TransactionResponse> failureResponse(String message) {
        return ApiResponse.<TransactionResponse>builder()
                .success(false)
                .message(message)
                .data(null)
                .build();
    }
}
