package com.bankingsystem.transaction.helper;

import com.bankingsystem.transaction.dto.TransactionDto;
import com.bankingsystem.transaction.entity.Transaction;
import com.bankingsystem.transaction.entity.TransactionStatus;
import com.bankingsystem.transaction.entity.TransactionType;

public class TransactionMapper {

    public TransactionDto toDto(Transaction transaction) {
        if (transaction == null)
            return null;

        return TransactionDto.builder()
                .id(transaction.getId())
                .accountId(transaction.getAccountId())
                .transactionDate(transaction.getTransactionDate())
                .amount(transaction.getAmount())
                .type(transaction.getType().name()) // convert Enum to String
                .description(transaction.getDescription())
                .relatedAccountId(transaction.getRelatedAccountId())
                .status(transaction.getStatus().name()) // convert Enum to String
                .referenceNumber(transaction.getReferenceNumber())
                .build();
    }

    public Transaction toEntity(TransactionDto dto) {
        if (dto == null)
            return null;

        return Transaction.builder()
                .id(dto.getId())
                .accountId(dto.getAccountId())
                .transactionDate(dto.getTransactionDate())
                .amount(dto.getAmount())
                .type(Enum.valueOf(TransactionType.class, dto.getType()))
                .description(dto.getDescription())
                .relatedAccountId(dto.getRelatedAccountId())
                .status(Enum.valueOf(TransactionStatus.class, dto.getStatus()))
                .referenceNumber(dto.getReferenceNumber())
                .build();
    }
}