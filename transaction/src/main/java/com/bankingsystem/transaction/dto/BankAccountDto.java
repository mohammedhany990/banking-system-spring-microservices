package com.bankingsystem.transaction.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankAccountDto {
private Long id;

    private Long customerId;

    private String accountNumber;

    private BigDecimal balance;

    private String accountType;

    private boolean active;
}
