package com.bankingsystem.transaction.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bankingsystem.transaction.client.AccountClient;
import com.bankingsystem.transaction.client.CustomerClient;
import com.bankingsystem.transaction.dto.BankAccountDto;
import com.bankingsystem.transaction.dto.CustomerDto;
import com.bankingsystem.transaction.dto.DepositRequest;
import com.bankingsystem.transaction.dto.TransactionDateRangeRequest;
import com.bankingsystem.transaction.dto.TransactionResponse;
import com.bankingsystem.transaction.dto.TransferRequest;
import com.bankingsystem.transaction.dto.WithdrawRequest;
import com.bankingsystem.transaction.entity.Transaction;
import com.bankingsystem.transaction.entity.TransactionStatus;
import com.bankingsystem.transaction.entity.TransactionType;
import com.bankingsystem.transaction.helper.ApiResponse;
import com.bankingsystem.transaction.helper.TransactionMapper;
import com.bankingsystem.transaction.repository.TransactionRepo;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepo transactionRepo;
    private final TransactionMapper transactionMapper;
    private final AccountClient accountClient;
    private final CustomerClient customerClient;

    @Override
    public TransactionResponse deposit(DepositRequest request) {
        if (request == null || request.getAccountId() == null || request.getAmount() == null
                || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid deposit request: accountId and positive amount are required.");
        }

        ApiResponse<BankAccountDto> accountResponse = accountClient.getAccountById(request.getAccountId());
        if (accountResponse == null || !accountResponse.isSuccess() || accountResponse.getData() == null) {
            throw new IllegalArgumentException("Account not found for id: " + request.getAccountId());
        }

        BankAccountDto account = accountResponse.getData();

        if (!account.isActive()) {
            throw new IllegalArgumentException("Cannot deposit to an inactive account");
        }

        ApiResponse<CustomerDto> customerResponse = customerClient.getCustomerById(account.getCustomerId());
        if (customerResponse == null || !customerResponse.isSuccess() || customerResponse.getData() == null) {
            throw new IllegalArgumentException("Customer not found for id: " + account.getCustomerId());
        }

        CustomerDto customer = customerResponse.getData();

        // Update balance
        BigDecimal newBalance = account.getBalance().add(request.getAmount());

        // Update account balance via AccountClient
        ApiResponse<BankAccountDto> updatedAccountResponse = accountClient.updateAccountBalance(account.getId(),
                newBalance);
        if (updatedAccountResponse == null || !updatedAccountResponse.isSuccess()
                || updatedAccountResponse.getData() == null) {
            throw new IllegalArgumentException("Failed to update account balance for id: " + account.getId());
        }

        BankAccountDto updatedAccount = updatedAccountResponse.getData();

        Transaction transaction = Transaction.builder()
                .accountId(request.getAccountId())
                .amount(request.getAmount())
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.SUCCESS)
                .transactionDate(LocalDateTime.now())
                .build();

        Transaction savedTransaction = transactionRepo.save(transaction);

        TransactionResponse response = transactionMapper.toResponse(
                savedTransaction,
                customer,
                updatedAccount.getBalance(),
                updatedAccount.getAccountNumber());

        // Add success info
        response.setSuccess(true);
        response.setMessage("Deposit successful");

        return response;
    }

    @Override
    public TransactionResponse withdraw(WithdrawRequest request) {

        if (request == null || request.getAccountId() == null || request.getAmount() == null
                || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid deposit request: accountId and positive amount are required.");
        }

        ApiResponse<BankAccountDto> accountResponse = accountClient.getAccountById(request.getAccountId());
        if (accountResponse == null || !accountResponse.isSuccess() || accountResponse.getData() == null) {
            throw new IllegalArgumentException("Account not found for id: " + request.getAccountId());
        }

        BankAccountDto account = accountResponse.getData();

        if (!account.isActive()) {
            throw new IllegalArgumentException("Cannot deposit to an inactive account");
        }

        ApiResponse<CustomerDto> customerResponse = customerClient.getCustomerById(account.getCustomerId());
        if (customerResponse == null || !customerResponse.isSuccess() || customerResponse.getData() == null) {
            throw new IllegalArgumentException("Customer not found for id: " + account.getCustomerId());
        }

        CustomerDto customer = customerResponse.getData();

        // Update balance
        BigDecimal newBalance = account.getBalance().subtract(request.getAmount());

        // Update account balance via AccountClient
        ApiResponse<BankAccountDto> updatedAccountResponse = accountClient.updateAccountBalance(account.getId(),
                newBalance);
        if (updatedAccountResponse == null || !updatedAccountResponse.isSuccess()
                || updatedAccountResponse.getData() == null) {
            throw new IllegalArgumentException("Failed to update account balance for id: " + account.getId());
        }

        BankAccountDto updatedAccount = updatedAccountResponse.getData();

        Transaction transaction = Transaction.builder()
                .accountId(request.getAccountId())
                .amount(request.getAmount())
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.SUCCESS)
                .transactionDate(LocalDateTime.now())
                .build();

        Transaction savedTransaction = transactionRepo.save(transaction);

        TransactionResponse response = transactionMapper.toResponse(
                savedTransaction,
                customer,
                updatedAccount.getBalance(),
                updatedAccount.getAccountNumber());

        response.setSuccess(true);
        response.setMessage("Withdraw successful");

        return response;

    }

    @Override
    public TransactionResponse transfer(TransferRequest request) {

        ApiResponse<BankAccountDto> senderResponse = accountClient.getAccountById(request.getFromAccountId());
        if (senderResponse == null || !senderResponse.isSuccess() || senderResponse.getData() == null) {
            throw new IllegalArgumentException("Account not found for id: " + request.getFromAccountId());
        }
        BankAccountDto sender = senderResponse.getData();

        ApiResponse<BankAccountDto> receiverResponse = accountClient.getAccountById(request.getToAccountId());
        if (receiverResponse == null || !receiverResponse.isSuccess() || receiverResponse.getData() == null) {
            throw new IllegalArgumentException("Account not found for id: " + request.getToAccountId());
        }
        BankAccountDto receiver = receiverResponse.getData();

        if (sender.getBalance().compareTo(request.getAmount()) < 0) {
            throw new IllegalArgumentException("");
        }

        BigDecimal newSenderBalance = sender.getBalance().subtract(request.getAmount());
        sender.setBalance(newSenderBalance);

        BigDecimal newReceiverBalance = receiver.getBalance().add(request.getAmount());
        receiver.setBalance(newReceiverBalance);

        ApiResponse<BankAccountDto> updatedReceiverResponse = accountClient
                .updateAccountBalance(request.getToAccountId(), newReceiverBalance);
        if (updatedReceiverResponse == null || !updatedReceiverResponse.isSuccess()
                || updatedReceiverResponse.getData() == null) {
            throw new IllegalArgumentException("Failed to update account balance for id: " + request.getToAccountId());
        }

        ApiResponse<BankAccountDto> updatedSenderResponse = accountClient
                .updateAccountBalance(request.getFromAccountId(), newSenderBalance);
        if (updatedSenderResponse == null || !updatedSenderResponse.isSuccess()
                || updatedSenderResponse.getData() == null) {
            throw new IllegalArgumentException("Failed to update account balance for id: " + request.getToAccountId());
        }

        ApiResponse<CustomerDto> customerResponse = customerClient.getCustomerById(sender.getCustomerId());
        if (customerResponse == null || !customerResponse.isSuccess() || customerResponse.getData() == null) {
            throw new IllegalArgumentException("Customer not found for id: " + sender.getCustomerId());
        }

        CustomerDto customer = customerResponse.getData();

        Transaction transaction = transactionMapper.fromTransferRequest(request);

        Transaction savedTransaction = transactionRepo.save(transaction);

        TransactionResponse response = transactionMapper.toResponse(
                savedTransaction,
                customer,
                newSenderBalance,
                sender.getAccountNumber());

        response.setSuccess(true);
        response.setMessage("Transfer successful");

        return response;

    }

    @Override
    public List<TransactionResponse> getTransactionsByAccountId(Long accountId) {
        List<Transaction> transactions = transactionRepo.findByAccountId(accountId);
        return transactionMapper.toResponseList(transactions);
    }

    @Override
    public List<TransactionResponse> getTransactionsBetweenDates(TransactionDateRangeRequest dateRangeRequest) {
        List<Transaction> transactions = transactionRepo.findByTransactionDateBetween(dateRangeRequest.getFrom(),
                dateRangeRequest.getTo());

        return transactionMapper.toResponseList(transactions);
    }

}