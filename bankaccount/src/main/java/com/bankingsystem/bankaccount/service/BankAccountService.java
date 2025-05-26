package com.bankingsystem.bankaccount.service;

import java.math.BigDecimal;
import java.util.List;

import com.bankingsystem.bankaccount.dto.BankAccountDto;
import com.bankingsystem.bankaccount.dto.CreateBankAccountDto;

public interface BankAccountService {
    BankAccountDto createAccount(CreateBankAccountDto dto);
    List<BankAccountDto> getAllAccounts();
    BankAccountDto getAccountById(Long id);
    void deleteAccount(Long id);
    BankAccountDto updateAccount(Long id, BankAccountDto dto);
    BankAccountDto activateAccount(Long id);
    BankAccountDto deactivateAccount(Long id);

    BankAccountDto updateAccountBalance(Long id, BigDecimal newBalance);



    List<BankAccountDto> getAccountsByAccountType(String accountType);
    
    List<BankAccountDto> getAccountsByAccountNumber(String accountNumber);
    

    List<BankAccountDto> getAccountsByCustomerId(Long customerId);

}
