package com.bankingsystem.transaction.controller;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bankingsystem.transaction.dto.TransactionDto;
import com.bankingsystem.transaction.helper.ApiResponse;
import com.bankingsystem.transaction.service.TransactionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<ApiResponse<TransactionDto>> createTransaction(@RequestBody TransactionDto transactionDto) {
        TransactionDto created = transactionService.createTransaction(transactionDto);

        ApiResponse<TransactionDto> response = ApiResponse.<TransactionDto>builder()
                .success(true)
                .message("Transaction created successfully")
                .data(created)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionDto>> getTransactionById(@PathVariable Long id) {
        TransactionDto dto = transactionService.getTransactionById(id);

        ApiResponse<TransactionDto> response = ApiResponse.<TransactionDto>builder()
                .success(true)
                .message("Transaction fetched successfully")
                .data(dto)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<ApiResponse<List<TransactionDto>>> getTransactionsByAccountId(@PathVariable Long accountId) {
        List<TransactionDto> transactions = transactionService.getTransactionsByAccountId(accountId);

        ApiResponse<List<TransactionDto>> response = ApiResponse.<List<TransactionDto>>builder()
                .success(true)
                .message("Transactions fetched successfully")
                .data(transactions)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TransactionDto>>> getAllTransactions() {
        List<TransactionDto> transactions = transactionService.getAllTransactions();

        ApiResponse<List<TransactionDto>> response = ApiResponse.<List<TransactionDto>>builder()
                .success(true)
                .message("All transactions fetched successfully")
                .data(transactions)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionDto>> updateTransaction(@PathVariable Long id,
                                                                         @RequestBody TransactionDto transactionDto) {
        TransactionDto updated = transactionService.updateTransaction(id, transactionDto);

        ApiResponse<TransactionDto> response = ApiResponse.<TransactionDto>builder()
                .success(true)
                .message("Transaction updated successfully")
                .data(updated)
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Transaction deleted successfully")
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<TransactionDto>>> getTransactionsByStatus(@PathVariable String status) {
        List<TransactionDto> transactions = transactionService.getTransactionsByStatus(status);

        ApiResponse<List<TransactionDto>> response = ApiResponse.<List<TransactionDto>>builder()
                .success(true)
                .message("Transactions filtered by status fetched successfully")
                .data(transactions)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<TransactionDto>>> getTransactionsByType(@PathVariable String type) {
        List<TransactionDto> transactions = transactionService.getTransactionsByType(type);

        ApiResponse<List<TransactionDto>> response = ApiResponse.<List<TransactionDto>>builder()
                .success(true)
                .message("Transactions filtered by type fetched successfully")
                .data(transactions)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/dates")
    public ResponseEntity<ApiResponse<List<TransactionDto>>> getTransactionsBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {

        List<TransactionDto> transactions = transactionService.getTransactionsBetweenDates(from, to);

        ApiResponse<List<TransactionDto>> response = ApiResponse.<List<TransactionDto>>builder()
                .success(true)
                .message("Transactions between dates fetched successfully")
                .data(transactions)
                .build();

        return ResponseEntity.ok(response);
    }
}
