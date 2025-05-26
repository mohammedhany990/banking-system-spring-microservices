package com.bankingsystem.bankaccount.helper;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.bankingsystem.bankaccount.dto.BankAccountDto;
import com.bankingsystem.bankaccount.dto.CreateBankAccountDto;
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
                .balance(bankAccount.getBalance() != null ? bankAccount.getBalance() : BigDecimal.ZERO)
                .accountType(bankAccount.getAccountType())
                .active(bankAccount.isActive())
                .build();
    }

    public BankAccount toEntity(BankAccountDto bankAccountDto) {
    
        return BankAccount.builder()
                .id(bankAccountDto.getId())
                .customerId(bankAccountDto.getCustomerId())
                .accountNumber(bankAccountDto.getAccountNumber())
                .balance(bankAccountDto.getBalance() != null ? bankAccountDto.getBalance() : BigDecimal.ZERO)   
                .accountType(bankAccountDto.getAccountType())
                .active(bankAccountDto.isActive())
                .build();
    }
    public BankAccount toEntity(CreateBankAccountDto createBankAccountDto) {
        if (createBankAccountDto == null) {
            return null;
        }
        return BankAccount.builder()
                .customerId(createBankAccountDto.getCustomerId())
                .balance(createBankAccountDto.getBalance() != null ? createBankAccountDto.getBalance() : BigDecimal.ZERO)
                .accountType(createBankAccountDto.getAccountType())
                .active(true)
                .build();
    }
    
}