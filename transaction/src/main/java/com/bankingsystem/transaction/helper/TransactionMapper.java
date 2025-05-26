package com.bankingsystem.transaction.helper;

import com.bankingsystem.transaction.dto.TransactionDto;
import com.bankingsystem.transaction.dto.TransactionResponse;
import com.bankingsystem.transaction.dto.TransferRequest;
import com.bankingsystem.transaction.dto.WithdrawRequest;
import com.bankingsystem.transaction.entity.Transaction;
import com.bankingsystem.transaction.entity.TransactionStatus;
import com.bankingsystem.transaction.entity.TransactionType;

// public class TransactionMapper {

//     public TransactionDto toDto(Transaction transaction) {
//         if (transaction == null)
//             return null;

//         return TransactionDto.builder()
//                 .id(transaction.getId())
//                 .accountId(transaction.getAccountId())
//                 .transactionDate(transaction.getTransactionDate())
//                 .amount(transaction.getAmount())
//                 .type(transaction.getType().name()) // convert Enum to String
//                 .description(transaction.getDescription())
//                 .relatedAccountId(transaction.getRelatedAccountId())
//                 .status(transaction.getStatus().name()) // convert Enum to String
//                 .referenceNumber(transaction.getReferenceNumber())
//                 .build();
//     }

//     public Transaction toEntity(TransactionDto dto) {
//         if (dto == null)
//             return null;

//         return Transaction.builder()
//                 .id(dto.getId())
//                 .accountId(dto.getAccountId())
//                 .transactionDate(dto.getTransactionDate())
//                 .amount(dto.getAmount())
//                 .type(Enum.valueOf(TransactionType.class, dto.getType()))
//                 .description(dto.getDescription())
//                 .relatedAccountId(dto.getRelatedAccountId())
//                 .status(Enum.valueOf(TransactionStatus.class, dto.getStatus()))
//                 .referenceNumber(dto.getReferenceNumber())
//                 .build();
//     }
// }

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

import com.bankingsystem.transaction.dto.CustomerDetails;
import com.bankingsystem.transaction.dto.CustomerDto;
import com.bankingsystem.transaction.dto.DepositRequest;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "transactionId", source = "transaction.id", qualifiedByName = "longToString")
    @Mapping(target = "accountId", source = "transaction.accountId")
    @Mapping(target = "relatedAccountId", source = "transaction.relatedAccountId")
    @Mapping(target = "amount", source = "transaction.amount")
    @Mapping(target = "balanceAfterTransaction", source = "balanceAfterTransaction")
    @Mapping(target = "transactionType", source = "transaction.type", qualifiedByName = "enumToString")
    @Mapping(target = "transactionStatus", source = "transaction.status", qualifiedByName = "enumToString")
    @Mapping(target = "transactionDate", source = "transaction.transactionDate")
    @Mapping(target = "referenceNumber", source = "transaction.referenceNumber")
    @Mapping(target = "customerName", source = "customerDto.fullName")
    @Mapping(target = "accountNumber", source = "accountNumber")
    @Mapping(target = "success", constant = "true")
    @Mapping(target = "message", constant = "Transaction successful")
    TransactionResponse toResponse(Transaction transaction, CustomerDto customerDto, BigDecimal balanceAfterTransaction,
            String accountNumber);

    @Named("longToString")
    static String longToString(Long id) {
        return id == null ? null : id.toString();
    }

    List<TransactionResponse> toResponseList(List<Transaction> transactions);

    default TransactionResponse toResponse(Transaction transaction) {
        return toResponse(transaction, null, null, null);
    }

    default Page<TransactionResponse> toResponsePage(Page<Transaction> page) {
        return page.map(this::toResponse);
    }

    // TransferRequest → Transaction
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transactionDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "type", constant = "TRANSFER")
    @Mapping(target = "status", constant = "SUCCESS")
    @Mapping(target = "accountId", source = "fromAccountId")
    @Mapping(target = "relatedAccountId", source = "toAccountId")
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "referenceNumber", ignore = true)
    Transaction fromTransferRequest(TransferRequest request);

    // WithdrawRequest → Transaction
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transactionDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "type", constant = "WITHDRAWAL")
    @Mapping(target = "status", constant = "SUCCESS")
    @Mapping(target = "accountId", source = "accountId")
    @Mapping(target = "relatedAccountId", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "referenceNumber", ignore = true)
    Transaction fromWithdrawRequest(WithdrawRequest request);

    // DepositRequest → Transaction
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transactionDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "type", constant = "DEPOSIT")
    @Mapping(target = "status", constant = "SUCCESS")
    @Mapping(target = "accountId", source = "accountId")
    @Mapping(target = "relatedAccountId", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "referenceNumber", ignore = true)
    Transaction fromDepositRequest(DepositRequest request);

    // Enum → String
    @Named("enumToString")
    default String enumToString(Enum<?> value) {
        return value != null ? value.name() : null;
    }
}
