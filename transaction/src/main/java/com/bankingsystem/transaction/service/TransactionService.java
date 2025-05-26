package com.bankingsystem.transaction.service;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;

import com.bankingsystem.transaction.dto.DepositRequest;
import com.bankingsystem.transaction.dto.TransactionDateRangeRequest;
import com.bankingsystem.transaction.dto.TransactionDto;
import com.bankingsystem.transaction.dto.TransactionResponse;
import com.bankingsystem.transaction.dto.TransferRequest;
import com.bankingsystem.transaction.dto.WithdrawRequest;
public interface TransactionService {

    
    TransactionResponse deposit(DepositRequest request);
    TransactionResponse withdraw(WithdrawRequest request);
    TransactionResponse transfer(TransferRequest request);

   
    List<TransactionResponse> getTransactionsByAccountId(Long accountId);
    List<TransactionResponse> getTransactionsBetweenDates(TransactionDateRangeRequest dateRangeRequest);

    // Admin-Only (If absolutely needed)
    // @AdminOnly
    // TransactionResponse forceCreateTransaction(TransactionRequest request);


    
}