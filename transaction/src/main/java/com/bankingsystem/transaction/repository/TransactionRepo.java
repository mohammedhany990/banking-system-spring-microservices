package com.bankingsystem.transaction.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bankingsystem.transaction.entity.Transaction;
import com.bankingsystem.transaction.entity.TransactionStatus;
import com.bankingsystem.transaction.entity.TransactionType;

public interface TransactionRepo extends JpaRepository<Transaction, Long> {
       
   List<Transaction> findByAccountId(Long accountId);

    List<Transaction> findByStatus(TransactionStatus status);

    List<Transaction> findByType(TransactionType type);

    List<Transaction> findByTransactionDateBetween(LocalDateTime from, LocalDateTime to);

}
