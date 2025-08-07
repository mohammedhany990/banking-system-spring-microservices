package com.bankingsystem.transaction.service;

import com.bankingsystem.transaction.client.AccountClient;
import com.bankingsystem.transaction.client.CustomerClient;
import com.bankingsystem.transaction.client.NotificationClient;
import com.bankingsystem.transaction.dto.*;
import com.bankingsystem.transaction.entity.Transaction;
import com.bankingsystem.transaction.entity.TransactionStatus;
import com.bankingsystem.transaction.entity.TransactionType;
import com.bankingsystem.transaction.exception.TransferException;
import com.bankingsystem.transaction.helper.ApiResponse;
import com.bankingsystem.transaction.helper.TransactionMapper;
import com.bankingsystem.transaction.repository.TransactionRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepo transactionRepo;
    private final TransactionMapper transactionMapper;
    private final AccountClient accountClient;
    private final CustomerClient customerClient;
    private final NotificationClient notificationClient;

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

        BigDecimal newBalance = account.getBalance().add(request.getAmount());

        UpdateBalanceRequest requestForNewBalance = UpdateBalanceRequest.builder().newBalance(newBalance).build();

        ApiResponse<BankAccountDto> updatedAccountResponse = accountClient.updateBalance(account.getId(), requestForNewBalance);

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
        response.setMessage("Deposit successful");

        try {
            notificationClient.createNotification(CreateNotificationDto.builder()
                    .customerId(customer.getId())
                    .customerEmail(customer.getEmail())
                    .title("Deposit Successful")
                    .type("TRANSACTION")
                    .message("Dear " + customer.getFirstName() + ", your deposit of " + request.getAmount()
                            + " was successful.")
                    .build());
        } catch (Exception e) {
            log.error("Failed to send deposit notification ", e);
        }

        return response;
    }

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

        BigDecimal newBalance = account.getBalance().subtract(request.getAmount());

        UpdateBalanceRequest requestForNewBalance = UpdateBalanceRequest.builder()
                .newBalance(newBalance)
                .build();
        ApiResponse<BankAccountDto> updatedAccountResponse = accountClient.updateBalance(account.getId(),
                requestForNewBalance);
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

        try {
            notificationClient.createNotification(CreateNotificationDto.builder()
                    .customerId(customer.getId())
                    .customerEmail(customer.getEmail())
                    .title("Withdrawal Successful")
                    .type("TRANSACTION")
                    .message("Dear " + customer.getFirstName() + ", your withdrawal of " + request.getAmount()
                            + " was successful.")
                    .build());
        } catch (Exception e) {
            log.error("Failed to send withdrawal notification ", e);
        }

        return response;

    }

    public TransactionResponse transfer(TransferRequest request) {

        ApiResponse<BankAccountDto> senderResponse = accountClient.getAccountById(request.getFromAccountId());
        if (senderResponse == null || !senderResponse.isSuccess() || senderResponse.getData() == null) {
            throw new TransferException("Account not found for id: " + request.getFromAccountId());
        }
        BankAccountDto sender = senderResponse.getData();

        ApiResponse<BankAccountDto> receiverResponse = accountClient.getAccountById(request.getToAccountId());
        if (receiverResponse == null || !receiverResponse.isSuccess() || receiverResponse.getData() == null) {
            throw new TransferException("Account not found for id: " + request.getToAccountId());
        }
        BankAccountDto receiver = receiverResponse.getData();

        if (sender.getBalance().compareTo(request.getAmount()) < 0) {
            throw new TransferException("Insufficient balance for transfer");
        }

        BigDecimal newSenderBalance = sender.getBalance().subtract(request.getAmount());
        sender.setBalance(newSenderBalance);

        BigDecimal newReceiverBalance = receiver.getBalance().add(request.getAmount());
        receiver.setBalance(newReceiverBalance);

        UpdateBalanceRequest requestForNewReceiverBalance = UpdateBalanceRequest.builder()
                .newBalance(newReceiverBalance)
                .build();

        ApiResponse<BankAccountDto> updatedReceiverResponse = accountClient
                .updateBalance(request.getToAccountId(), requestForNewReceiverBalance);
        if (updatedReceiverResponse == null || !updatedReceiverResponse.isSuccess()
                || updatedReceiverResponse.getData() == null) {
            throw new TransferException("Failed to update account balance for id: " + request.getToAccountId());
        }
        UpdateBalanceRequest requestForNewSenderBalance = UpdateBalanceRequest.builder()
                .newBalance(newSenderBalance)
                .build();

        ApiResponse<BankAccountDto> updatedSenderResponse = accountClient
                .updateBalance(request.getFromAccountId(), requestForNewSenderBalance);
        if (updatedSenderResponse == null || !updatedSenderResponse.isSuccess()
                || updatedSenderResponse.getData() == null) {
            throw new TransferException(
                    "Failed to update account balance for id: " + request.getFromAccountId());
        }

        ApiResponse<CustomerDto> senderCustomerResponse = customerClient.getCustomerById(sender.getCustomerId());

        if (senderCustomerResponse == null || !senderCustomerResponse.isSuccess()
                || senderCustomerResponse.getData() == null) {
            throw new TransferException("Customer not found for id: " + sender.getCustomerId());
        }
        CustomerDto senderCustomer = senderCustomerResponse.getData();

        ApiResponse<CustomerDto> receiverCustomerResponse = customerClient.getCustomerById(receiver.getCustomerId());

        if (receiverCustomerResponse == null || !receiverCustomerResponse.isSuccess()
                || receiverCustomerResponse.getData() == null) {
            throw new TransferException("Customer not found for id: " + receiver.getCustomerId());
        }
        CustomerDto receiverCustomer = receiverCustomerResponse.getData();

        Transaction transaction = transactionMapper.fromTransferRequest(request);
        Transaction savedTransaction = transactionRepo.save(transaction);

        TransactionResponse response = transactionMapper.toResponse(
                savedTransaction,
                senderCustomer,
                newSenderBalance,
                sender.getAccountNumber());

        response.setSuccess(true);
        response.setMessage("Transfer successful");

        try {
            notificationClient.createNotification(CreateNotificationDto.builder()
                    .customerId(senderCustomer.getId())
                    .customerEmail(senderCustomer.getEmail())
                    .title("Transfer Successful")
                    .type("TRANSACTION")
                    .message("Dear " + senderCustomer.getFirstName() + ", your transfer of " + request.getAmount()
                            + " to account " + receiver.getAccountNumber() + " was successful.")
                    .build());

            notificationClient.createNotification(CreateNotificationDto.builder()
                    .customerId(receiverCustomer.getId())
                    .customerEmail(receiverCustomer.getEmail())
                    .title("Received Transfer")
                    .type("TRANSACTION")
                    .message("Dear " + receiverCustomer.getFirstName() + ", you have received " + request.getAmount()
                            + " from account " + sender.getAccountNumber() + ".")
                    .build());
        } catch (Exception e) {
            log.error("Failed to send transfer notifications", e);
        }

        return response;
    }

    public List<TransactionResponse> getTransactionsByAccountId(Long accountId) {
        List<Transaction> transactions = transactionRepo.findByAccountId(accountId);
        return transactionMapper.toResponseList(transactions);
    }

    public List<TransactionResponse> getTransactionsBetweenDates(TransactionDateRangeRequest dateRangeRequest) {
        List<Transaction> transactions = transactionRepo.findByTransactionDateBetween(dateRangeRequest.getFrom(),
                dateRangeRequest.getTo());

        return transactionMapper.toResponseList(transactions);
    }

}