package com.bankingsystem.bankaccount.dto;

import java.math.BigDecimal;

import com.bankingsystem.bankaccount.entity.AccountType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankAccountDto {

    private Long id;

    private Long customerId;

    private String accountNumber;

    private BigDecimal balance;

    private AccountType accountType;

    private boolean active;
}