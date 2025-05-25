package com.bankingsystem.transaction.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bankingsystem.transaction.client.AccountClient;
import com.bankingsystem.transaction.client.CustomerClient;
import com.bankingsystem.transaction.dto.CustomerDto;
import com.bankingsystem.transaction.dto.TransactionDto;
import com.bankingsystem.transaction.helper.TransactionMapper;
import com.bankingsystem.transaction.repository.TransactionRepo;
import com.bankingsystem.transaction.entity.Transaction;
import com.bankingsystem.transaction.entity.TransactionStatus;
import com.bankingsystem.transaction.entity.TransactionType;
import com.bankingsystem.transaction.exception.InvalidTransactionException;
import com.bankingsystem.transaction.exception.TransactionNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepo transactionRepo;
    private final TransactionMapper transactionMapper;
    private final AccountClient accountClient;
    private final CustomerClient customerClient;

    @Override
    public TransactionDto createTransaction(TransactionDto transactionDto) {
        if (transactionDto == null) {
            throw new InvalidTransactionException("Transaction data must be provided");
        }

        if (transactionDto.getAmount() == null || transactionDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Transaction amount must be greater than zero");
        }
        if (transactionDto.getType() == null || transactionDto.getType().trim().isEmpty()) {
            throw new InvalidTransactionException("Transaction type must be provided");
        }
        if (transactionDto.getStatus() == null || transactionDto.getStatus().trim().isEmpty()) {
            throw new InvalidTransactionException("Transaction status must be provided");
        }
        TransactionType transactionType = parseTransactionType(transactionDto.getType());
        TransactionStatus transactionStatus = parseTransactionStatus(transactionDto.getStatus());

        String accountResponse = accountClient.getAccountById(transactionDto.getAccountId());
        if (accountResponse == null) {
            throw new InvalidTransactionException("Account not found for id: " + transactionDto.getAccountId());
        }

        // ✅ Example: check if customer exists
        CustomerDto customerDto = customerClient.getCustomerById(transactionDto.getCustomerId());
        if (customerDto == null) {
            throw new InvalidTransactionException("Customer not found for id: " + transactionDto.getCustomerId());
        }

        Transaction transaction = transactionMapper.toEntity(transactionDto);
        transaction.setTransactionDate(LocalDateTime.now());

        Transaction saved = transactionRepo.save(transaction);
        return transactionMapper.toDto(saved);
    }

    @Override
    public TransactionDto getTransactionById(Long id) {
        return transactionRepo.findById(id)
                .map(transactionMapper::toDto)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction with id " + id + " not found"));
    }

    @Override
    public List<TransactionDto> getTransactionsByAccountId(Long accountId) {
        return transactionRepo.findByAccountId(accountId)
                .stream()
                .map(transactionMapper::toDto)
                .toList();
    }

    @Override
    public List<TransactionDto> getAllTransactions() {
        return transactionRepo.findAll()
                .stream()
                .map(transactionMapper::toDto)
                .toList();
    }

    @Override
    public TransactionDto updateTransaction(Long id, TransactionDto transactionDto) {
        if (transactionDto == null) {
            throw new InvalidTransactionException("Transaction data must be provided");
        }

        Transaction existingTransaction = transactionRepo.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction with id " + id + " not found"));

        TransactionType transactionType = parseTransactionType(transactionDto.getType());

        TransactionStatus transactionStatus = parseTransactionStatus(transactionDto.getStatus());

        if (transactionDto.getAmount() == null || transactionDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Transaction amount must be greater than zero");
        }

        if (transactionDto.getDescription() != null && transactionDto.getDescription().length() > 250) {
            throw new InvalidTransactionException("Description cannot exceed 250 characters");
        }

        existingTransaction.setAmount(transactionDto.getAmount());
        existingTransaction.setType(transactionType);
        existingTransaction.setStatus(transactionStatus);
        existingTransaction.setDescription(transactionDto.getDescription());

        Transaction updatedTransaction = transactionRepo.save(existingTransaction);

        return transactionMapper.toDto(updatedTransaction);
    }

    @Override
    public void deleteTransaction(Long id) {
        if (!transactionRepo.existsById(id)) {
            throw new TransactionNotFoundException("Transaction with id " + id + " not found");
        }
        transactionRepo.deleteById(id);
    }

    @Override
    public List<TransactionDto> getTransactionsByStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new InvalidTransactionException("Transaction status cannot be null or empty");
        }

        TransactionStatus transactionStatus = parseTransactionStatus(status);

        return transactionRepo.findByStatus(transactionStatus)
                .stream()
                .map(transactionMapper::toDto)
                .toList();
    }

    @Override
    public List<TransactionDto> getTransactionsByType(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new InvalidTransactionException("Transaction type cannot be null or empty");
        }

        TransactionType transactionType = parseTransactionType(type);

        return transactionRepo.findByType(transactionType)
                .stream()
                .map(transactionMapper::toDto)
                .toList();
    }

    @Override
    public List<TransactionDto> getTransactionsBetweenDates(LocalDateTime from, LocalDateTime to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Both start date and end date must be provided");
        }

        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        return transactionRepo.findByTransactionDateBetween(from, to)
                .stream()
                .map(transactionMapper::toDto)
                .toList();
    }

    private TransactionType parseTransactionType(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new InvalidTransactionException("Transaction type cannot be null or empty");
        }
        try {
            return TransactionType.valueOf(type.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidTransactionException("Invalid transaction type: " + type);
        }
    }

    private TransactionStatus parseTransactionStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new InvalidTransactionException("Transaction status cannot be null or empty");
        }
        try {
            return TransactionStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidTransactionException("Invalid transaction status: " + status);
        }
    }
}
