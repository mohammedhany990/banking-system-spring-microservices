package com.bankingsystem.bankaccount.helper;

import org.springframework.stereotype.Component;

import com.bankingsystem.bankaccount.dto.BankAccountDto;
import com.bankingsystem.bankaccount.entity.BankAccount;

@Component
public class BankAccountMapper {

    public BankAccountDto toDto(BankAccount bankAccount) {
        if (bankAccount == null) {
            return null;
        }
        return BankAccountDto.builder()
                .id(bankAccount.getId())
                .customerId(bankAccount.getCustomerId())
                .accountNumber(bankAccount.getAccountNumber())
                .balance(bankAccount.getBalance())
                .accountType(bankAccount.getAccountType())
                .active(bankAccount.isActive())
                .build();
    }

    public BankAccount toEntity(BankAccountDto bankAccountDto) {
    
        return BankAccount.builder()
                .id(bankAccountDto.getId())
                .customerId(bankAccountDto.getCustomerId())
                .accountNumber(bankAccountDto.getAccountNumber())
                .balance(bankAccountDto.getBalance())
                .accountType(bankAccountDto.getAccountType())
                .active(bankAccountDto.isActive())
                .build();
    }
}