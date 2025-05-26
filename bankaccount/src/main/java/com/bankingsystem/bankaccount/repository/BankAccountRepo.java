package com.bankingsystem.bankaccount.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bankingsystem.bankaccount.entity.AccountType;
import com.bankingsystem.bankaccount.entity.BankAccount;

@Repository
public interface BankAccountRepo extends JpaRepository<BankAccount, Long> {
    
    List<BankAccount> findByCustomerId(Long customerId);
    Optional<BankAccount> findByCustomerIdAndAccountType(Long customerId, AccountType accountType);

    
    List<BankAccount> findByAccountNumber(String accountNumber);
 
    List<BankAccount> findByAccountType(AccountType accountType);
  
}
