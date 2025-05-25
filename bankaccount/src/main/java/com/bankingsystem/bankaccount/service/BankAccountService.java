package com.bankingsystem.bankaccount.service;

import java.util.List;

import com.bankingsystem.bankaccount.dto.BankAccountDto;

public interface BankAccountService {
    BankAccountDto createAccount(BankAccountDto dto);
    List<BankAccountDto> getAllAccounts();
    BankAccountDto getAccountById(Long id);
    void deleteAccount(Long id);
    BankAccountDto updateAccount(Long id, BankAccountDto dto);
    BankAccountDto activateAccount(Long id);
    BankAccountDto deactivateAccount(Long id);

    List<BankAccountDto> getAccountsByAccountType(String accountType);
    
    List<BankAccountDto> getAccountsByAccountNumber(String accountNumber);
    
    List<BankAccountDto> getAccountsByCustomerId(Long customerId);

}
