package com.bankingsystem.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    private Long accountId;

    private LocalDateTime transactionDate;

    private BigDecimal amount;

    private String type;

    private String description;

    
    private Long relatedAccountId;

    private String status;

    private String referenceNumber;

    private Long customerId;

    private String customerUsername;
}
