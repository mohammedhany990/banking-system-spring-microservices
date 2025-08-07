package com.bankingsystem.card.client;


import com.bankingsystem.card.dto.TransactionResponse;
import com.bankingsystem.card.dto.transactions.DepositRequest;
import com.bankingsystem.card.dto.transactions.TransferRequest;
import com.bankingsystem.card.dto.transactions.WithdrawRequest;
import com.bankingsystem.card.helper.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "transaction-service")
public interface TransactionClient {

    @PostMapping("/deposit")
    ApiResponse<TransactionResponse> deposit(@RequestBody DepositRequest depositRequest);

    @PostMapping("/withdraw")
    ApiResponse<TransactionResponse> withdraw(@RequestBody WithdrawRequest withdrawRequest);

    @PostMapping("/transfer")
    ApiResponse<TransactionResponse> transfer(@Valid @RequestBody TransferRequest transferRequest);
}