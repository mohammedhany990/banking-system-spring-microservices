package com.bankingsystem.transaction.service;

import java.time.LocalDateTime;
import java.util.List;

import com.bankingsystem.transaction.dto.TransactionDto;

    public interface TransactionService {

        TransactionDto createTransaction(TransactionDto transactionDto);

        TransactionDto getTransactionById(Long id);

        List<TransactionDto> getTransactionsByAccountId(Long accountId);

        List<TransactionDto> getAllTransactions();

        TransactionDto updateTransaction(Long id, TransactionDto transactionDto);

        void deleteTransaction(Long id);

        List<TransactionDto> getTransactionsByStatus(String status);

        List<TransactionDto> getTransactionsByType(String type);

        List<TransactionDto> getTransactionsBetweenDates(LocalDateTime from, LocalDateTime to);

    }