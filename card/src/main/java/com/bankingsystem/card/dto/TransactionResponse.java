package com.bankingsystem.card.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {

    private String transactionId; // Unique transaction reference
    private Long accountId; // Primary account involved
    private Long relatedAccountId; // For transfers (nullable)

    private BigDecimal amount;
    private BigDecimal balanceAfterTransaction; // New balance after operation

    private String transactionType; // Enum as String (DEPOSIT, WITHDRAWAL, TRANSFER)

    private String transactionStatus; // Enum as String (SUCCESS, FAILED, PENDING)

    private LocalDateTime transactionDate;

    private String referenceNumber; // Bank reference number

    // Additional metadata
    private String customerName; // For client display

    private String accountNumber; // Masked for security

    // Standardized response fields
    private boolean success;

    private String message; // "Deposit successful", "Insufficient funds", etc.

    // Error details (if applicable)
    private String errorCode;

    private String errorDetails;
}