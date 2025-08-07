package com.bankingsystem.bankaccount.service;

import com.bankingsystem.bankaccount.client.CustomerClient;
import com.bankingsystem.bankaccount.client.NotificationClient;
import com.bankingsystem.bankaccount.dto.BankAccountDto;
import com.bankingsystem.bankaccount.dto.CreateBankAccountDto;
import com.bankingsystem.bankaccount.dto.CreateNotificationDto;
import com.bankingsystem.bankaccount.dto.CustomerDto;
import com.bankingsystem.bankaccount.entity.AccountType;
import com.bankingsystem.bankaccount.entity.BankAccount;
import com.bankingsystem.bankaccount.exception.BankAccountAlreadyExistsException;
import com.bankingsystem.bankaccount.exception.BankAccountNotFoundException;
import com.bankingsystem.bankaccount.exception.InvalidBankAccountOperationException;
import com.bankingsystem.bankaccount.helper.ApiResponse;
import com.bankingsystem.bankaccount.helper.BankAccountMapper;
import com.bankingsystem.bankaccount.repository.BankAccountRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankAccountService {

    private static final String NOTIFICATION_ACCOUNT_CREATED =
            "Hello {0}, your account has been successfully created. Enjoy our services!\n\nYour Bank Account Number: {1}\nYour Balance: {2}";
    private static final String NOTIFICATION_ACCOUNT_UPDATED =
            "Hello {0}, your bank account with number {1} has been updated successfully.\n\nNew Balance: {2}";
    private static final String NOTIFICATION_ACCOUNT_CLOSED =
            "Hello {0}, your bank account with number {1} has been successfully closed.\n\nThank you for using our services.";
    private static final String NOTIFICATION_ACCOUNT_STATUS =
            "Hello {0}, your bank account with number {1} has been {2} successfully.";


    private final BankAccountRepo bankAccountRepo;
    private final BankAccountMapper bankAccountMapper;
    private final CustomerClient customerClient;
    private final NotificationClient notificationClient;

    @Transactional
    public BankAccountDto createAccount(CreateBankAccountDto dto) {
        if (dto.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidBankAccountOperationException("Initial balance cannot be negative");
        }
        if (dto.getCustomerId() <= 0) {
            throw new InvalidBankAccountOperationException("Invalid customer ID");
        }

        Optional<BankAccount> existingAccount = bankAccountRepo.findByCustomerIdAndAccountType(
                dto.getCustomerId(), dto.getAccountType());
        if (existingAccount.isPresent()) {
            throw new BankAccountAlreadyExistsException("Customer already has a " + dto.getAccountType() + " account.");
        }

        ApiResponse<CustomerDto> customerResponse = customerClient.getCustomerById(dto.getCustomerId());
        if (customerResponse == null) {
            throw new RuntimeException("Customer service is unavailable");
        } else if (!customerResponse.isSuccess() || customerResponse.getData() == null) {
            throw new BankAccountNotFoundException("Customer not found with id: " + dto.getCustomerId());
        }

        CustomerDto customer = customerResponse.getData();
        BankAccount bankAccount = bankAccountMapper.toEntity(dto);
        bankAccount.setActive(true);
        bankAccount.setAccountNumber(generateAccountNumber());

        BankAccount savedAccount = bankAccountRepo.saveAndFlush(bankAccount);

        try {
            notificationClient.createNotification(CreateNotificationDto.builder()
                    .customerId(customer.getId())
                    .customerEmail(customer.getEmail())
                    .title("Welcome to Our Platform!")
                    .type("ACCOUNT")
                    .message(MessageFormat.format(
                            NOTIFICATION_ACCOUNT_CREATED,
                            customer.getUsername(),
                            savedAccount.getAccountNumber(),
                            savedAccount.getBalance()))
                    .build());
            log.info("Notification sent successfully for customer id {} and account id {}",
                    customer.getId(), savedAccount.getId());
        } catch (Exception e) {
            log.error("Failed to send notification for customer id {} and account id {}",
                    customer.getId(), savedAccount.getId(), e);
        }

        return bankAccountMapper.toDto(savedAccount);
    }

    @Transactional(readOnly = true)
    public List<BankAccountDto> getAllAccounts() {
        return bankAccountRepo.findAll()
                .stream()
                .map(bankAccountMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public BankAccountDto getAccountById(Long id) {
        return bankAccountRepo.findById(id)
                .map(bankAccountMapper::toDto)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank account not found with id: " + id));
    }

    @Transactional
    public void deleteAccount(Long id) {
        BankAccount bankAccount = bankAccountRepo.findById(id)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank account not found with id: " + id));

        ApiResponse<CustomerDto> customerResponse = customerClient.getCustomerById(bankAccount.getCustomerId());
        if (customerResponse == null) {
            throw new RuntimeException("Customer service is unavailable");
        } else if (!customerResponse.isSuccess() || customerResponse.getData() == null) {
            throw new BankAccountNotFoundException("Customer not found with id: " + bankAccount.getCustomerId());
        }

        CustomerDto customer = customerResponse.getData();

        try {
            notificationClient.createNotification(CreateNotificationDto.builder()
                    .customerId(bankAccount.getCustomerId())
                    .customerEmail(customer.getEmail())
                    .title("Bank Account Closure Notice")
                    .type("ACCOUNT")
                    .message(MessageFormat.format(
                            NOTIFICATION_ACCOUNT_CLOSED,
                            customer.getUsername(),
                            bankAccount.getAccountNumber()))
                    .build());
            log.info("Account closure notification sent successfully for customer id {} and account id {}",
                    customer.getId(), id);
        } catch (Exception e) {
            log.error("Failed to send account closure notification for customer id {} and account id {}",
                    customer.getId(), id, e);
        }

        bankAccountRepo.deleteById(id);
    }

    @Transactional
    public BankAccountDto updateAccount(Long id, BankAccountDto dto) {
        BankAccount existingAccount = bankAccountRepo.findById(id)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank account not found with id: " + id));

        existingAccount.setBalance(dto.getBalance());
        existingAccount.setAccountType(dto.getAccountType());
        existingAccount.setActive(dto.isActive());

        BankAccount updatedAccount = bankAccountRepo.save(existingAccount);

        ApiResponse<CustomerDto> customerResponse = customerClient.getCustomerById(existingAccount.getCustomerId());
        if (customerResponse == null) {
            throw new RuntimeException("Customer service is unavailable");
        } else if (!customerResponse.isSuccess() || customerResponse.getData() == null) {
            throw new BankAccountNotFoundException("Customer not found with id: " + existingAccount.getCustomerId());
        }

        CustomerDto customer = customerResponse.getData();

        try {
            notificationClient.createNotification(CreateNotificationDto.builder()
                    .customerId(customer.getId())
                    .customerEmail(customer.getEmail())
                    .title("Bank Account Update Notice")
                    .type("ACCOUNT")
                    .message(MessageFormat.format(
                            NOTIFICATION_ACCOUNT_UPDATED,
                            customer.getUsername(),
                            existingAccount.getAccountNumber(),
                            existingAccount.getBalance()))
                    .build());
            log.info("Account update notification sent successfully for customer id {} and account id {}",
                    customer.getId(), id);
        } catch (Exception e) {
            log.error("Failed to send account update notification for customer id {} and account id {}",
                    customer.getId(), id, e);
        }

        return bankAccountMapper.toDto(updatedAccount);
    }

    @Transactional(readOnly = true)
    public List<BankAccountDto> getAccountsByCustomerId(Long customerId) {
        if (customerId <= 0) {
            throw new InvalidBankAccountOperationException("Invalid customer ID");
        }
        return bankAccountRepo.findByCustomerId(customerId)
                .stream()
                .map(bankAccountMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BankAccountDto> getAccountsByAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new InvalidBankAccountOperationException("Account number cannot be null or empty");
        }
        return bankAccountRepo.findByAccountNumber(accountNumber)
                .stream()
                .map(bankAccountMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BankAccountDto> getAccountsByAccountType(String accountType) {
        if (accountType == null || accountType.trim().isEmpty()) {
            throw new InvalidBankAccountOperationException("Account type cannot be null or empty");
        }
        try {
            AccountType type = AccountType.valueOf(accountType.toUpperCase());
            return bankAccountRepo.findByAccountType(type)
                    .stream()
                    .map(bankAccountMapper::toDto)
                    .toList();
        } catch (IllegalArgumentException e) {
            throw new InvalidBankAccountOperationException("Invalid account type: " + accountType);
        }
    }

    @Transactional
    public BankAccountDto updateAccountBalance(Long id, BigDecimal newBalance) {
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidBankAccountOperationException("New balance cannot be negative");
        }
        BankAccount bankAccount = bankAccountRepo.findById(id)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank account not found with id: " + id));

        bankAccount.setBalance(newBalance);
        BankAccount updatedAccount = bankAccountRepo.save(bankAccount);

        ApiResponse<CustomerDto> customerResponse = customerClient.getCustomerById(bankAccount.getCustomerId());
        if (customerResponse == null) {
            throw new RuntimeException("Customer service is unavailable");
        } else if (!customerResponse.isSuccess() || customerResponse.getData() == null) {
            throw new BankAccountNotFoundException("Customer not found with id: " + bankAccount.getCustomerId());
        }
        CustomerDto customer = customerResponse.getData();

        try {
            notificationClient.createNotification(CreateNotificationDto.builder()
                    .customerId(customer.getId())
                    .customerEmail(customer.getEmail())
                    .title("Bank Account Balance Update Notice")
                    .type("ACCOUNT")
                    .message(MessageFormat.format(
                            NOTIFICATION_ACCOUNT_UPDATED,
                            customer.getUsername(),
                            bankAccount.getAccountNumber(),
                            updatedAccount.getBalance()))
                    .build());
            log.info("Balance update notification sent successfully for customer id {} and account id {}",
                    customer.getId(), id);
        } catch (Exception e) {
            log.error("Failed to send balance update notification for customer id {} and account id {}",
                    customer.getId(), id, e);
        }

        return bankAccountMapper.toDto(updatedAccount);
    }

    @Transactional
    public BankAccountDto activateAccount(Long id) {
        BankAccount bankAccount = bankAccountRepo.findById(id)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank account not found with id: " + id));

        bankAccount.setActive(true);
        BankAccount updatedAccount = bankAccountRepo.save(bankAccount);

        sendAccountStatusNotification(updatedAccount, true);

        return bankAccountMapper.toDto(updatedAccount);
    }

    @Transactional
    public BankAccountDto deactivateAccount(Long id) {
        BankAccount bankAccount = bankAccountRepo.findById(id)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank account not found with id: " + id));

        bankAccount.setActive(false);
        BankAccount updatedAccount = bankAccountRepo.save(bankAccount);

        sendAccountStatusNotification(updatedAccount, false);

        return bankAccountMapper.toDto(updatedAccount);
    }

    @Async
    private void sendAccountStatusNotification(BankAccount account, boolean activated) {
        ApiResponse<CustomerDto> customerResponse = customerClient.getCustomerById(account.getCustomerId());
        if (customerResponse == null) {
            log.error("Failed to send notification: Customer service unavailable for customer id {} and account id {}",
                    account.getCustomerId(), account.getId());
            return;
        } else if (!customerResponse.isSuccess() || customerResponse.getData() == null) {
            log.error("Failed to send notification: Customer not found with id {} for account id {}",
                    account.getCustomerId(), account.getId());
            return;
        }
        CustomerDto customer = customerResponse.getData();

        String status = activated ? "activated" : "deactivated";
        String title = "Bank Account " + (activated ? "Activation" : "Deactivation") + " Notice";

        try {
            notificationClient.createNotification(CreateNotificationDto.builder()
                    .customerId(customer.getId())
                    .customerEmail(customer.getEmail())
                    .title(title)
                    .type("ACCOUNT")
                    .message(MessageFormat.format(
                            NOTIFICATION_ACCOUNT_STATUS,
                            customer.getUsername(),
                            account.getAccountNumber(),
                            status))
                    .build());
            log.info("Account {} notification sent successfully for customer id {} and account id {}",
                    status, customer.getId(), account.getId());
        } catch (Exception e) {
            log.error("Failed to send account {} notification for customer id {} and account id {}",
                    status, customer.getId(), account.getId(), e);
        }
    }

    private String generateAccountNumber() {
        Year currentYear = Year.now();
        String year = String.valueOf(currentYear);
        for (int attempts = 0; attempts < 10; attempts++) {
            String random = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
            String accountNumber = year + random;
            if (!bankAccountRepo.existsByAccountNumber(accountNumber)) {
                return accountNumber;
            }
        }
        throw new RuntimeException("Failed to generate unique account number after multiple attempts");
    }
}
