package com.bankingsystem.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDto {

    private Long id;  

    @NotNull(message = "Account ID is required")
    private Long accountId;

    @NotNull(message = "Transaction date is required")
    private LocalDateTime transactionDate;

    @NotNull(message = "Amount is required")
    @Digits(integer = 16, fraction = 2, message = "Invalid amount format")
    private BigDecimal amount;

    @NotNull(message = "Transaction type is required")
    private String type;

    @Size(max = 250, message = "Description can be up to 250 characters")
    private String description;

    private Long relatedAccountId;

    @NotNull(message = "Transaction status is required")
    private String status;

    @Size(max = 100, message = "Reference number can be up to 100 characters")
    private String referenceNumber;
    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @Size(max = 100, message = "Customer username can be up to 100 characters")
    private String customerUsername;

    @Size(max = 100, message = "Customer email can be up to 100 characters")
    private String customerEmail;
    
    @Size(max = 100, message = "Customer phone can be up to 100 characters")    
    private String customerPhone;
}