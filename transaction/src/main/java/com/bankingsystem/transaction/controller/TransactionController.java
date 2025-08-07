package com.bankingsystem.transaction.controller;

import java.util.List;

import com.bankingsystem.transaction.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bankingsystem.transaction.dto.DepositRequest;
import com.bankingsystem.transaction.dto.TransactionDateRangeRequest;
import com.bankingsystem.transaction.dto.TransactionResponse;
import com.bankingsystem.transaction.dto.TransferRequest;
import com.bankingsystem.transaction.dto.WithdrawRequest;
import com.bankingsystem.transaction.helper.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<TransactionResponse>> deposit(@RequestBody DepositRequest depositRequest) {
        TransactionResponse response = transactionService.deposit(depositRequest);
        ApiResponse<TransactionResponse> apiResponse = ApiResponse.<TransactionResponse>builder()
                .success(true)
                .message("Deposit successful")
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);

    }

    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<TransactionResponse>> withdraw(@RequestBody WithdrawRequest withdrawRequest) {
        TransactionResponse response = transactionService.withdraw(withdrawRequest);
        ApiResponse<TransactionResponse> apiResponse = ApiResponse.<TransactionResponse>builder()
                .success(true)
                .message("Deposit successful")
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);

    }

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<TransactionResponse>> transfer(
            @Valid @RequestBody TransferRequest transferRequest) {

        TransactionResponse response = transactionService.transfer(transferRequest);
        ApiResponse<TransactionResponse> apiResponse = ApiResponse.<TransactionResponse>builder()
                .success(true)
                .message("Transfer successful")
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/transactions-between")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactionsBetweenDates(
            @RequestBody TransactionDateRangeRequest dateRangeRequest) {

        List<TransactionResponse> responseList = transactionService.getTransactionsBetweenDates(dateRangeRequest);
                

        ApiResponse<List<TransactionResponse>> apiResponse = ApiResponse.<List<TransactionResponse>>builder()
                .success(true)
                .message("Transactions fetched successfully")
                .data(responseList)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

}
