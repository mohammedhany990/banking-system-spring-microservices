package com.bankingsystem.bankaccount.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bankingsystem.bankaccount.entity.AccountType;
import com.bankingsystem.bankaccount.entity.BankAccount;


public interface BankAccountRepo extends JpaRepository<BankAccount, Long> {
    
    List<BankAccount> findByCustomerId(Long customerId);
    
    List<BankAccount> findByAccountNumber(String accountNumber);
 
    List<BankAccount> findByAccountType(AccountType accountType);
  
}
