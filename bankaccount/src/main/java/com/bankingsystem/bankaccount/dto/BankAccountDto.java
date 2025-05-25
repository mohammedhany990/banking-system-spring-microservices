package com.bankingsystem.bankaccount.dto;

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

    private double balance;

    private AccountType accountType;

    private boolean active;
}