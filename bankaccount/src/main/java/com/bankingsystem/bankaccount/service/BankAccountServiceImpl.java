package com.bankingsystem.bankaccount.service;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

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

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepo bankAccountRepo;
    private final BankAccountMapper bankAccountMapper;
    private final CustomerClient customerClient;
    private final NotificationClient notificationClient;

    @Override
    public BankAccountDto createAccount(CreateBankAccountDto dto) {

        if (dto.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidBankAccountOperationException("Initial balance cannot be negative");
        }

        Optional<BankAccount> existingAccount = bankAccountRepo.findByCustomerIdAndAccountType(
                dto.getCustomerId(), dto.getAccountType());

        if (existingAccount.isPresent()) {
            throw new BankAccountAlreadyExistsException("Customer already has a " + dto.getAccountType() + " account.");
        }

        ApiResponse<CustomerDto> customerResponse = customerClient.getCustomerById(dto.getCustomerId()).block();

        if (customerResponse == null || !customerResponse.isSuccess() || customerResponse.getData() == null) {
            throw new BankAccountNotFoundException("Customer not found with id: " + dto.getCustomerId());
        }

        CustomerDto customer = customerResponse.getData();

        BankAccount bankAccount = bankAccountMapper.toEntity(dto);

        bankAccount.setActive(true);

        bankAccount.setAccountNumber(generateAccountNumber());

        BankAccount savedAccount = bankAccountRepo.save(bankAccount);

        try {
            notificationClient.sendNotificationAsync(CreateNotificationDto.builder()
                    .customerId(customer.getId())
                    .customerEmail(customer.getEmail())
                    .title("Welcome to Our Platform!\n\n")
                    .type("ACCOUNT")
                    .message("Hello " + customer.getUsername()
                            + ", your account has been successfully created. Enjoy our services!\n\n" +
                            "Your Bank Account Number: " + savedAccount.getAccountNumber() + "\n" +
                            "Your Balance: " + savedAccount.getBalance())
                    .build());

            log.info("Notification sent successfully for customer id {}", customer.getId());
        } catch (Exception e) {
            log.error("Failed to send notification for customer id {}", customer.getId(), e);
        }

        return bankAccountMapper.toDto(savedAccount);
    }

    @Override
    public List<BankAccountDto> getAllAccounts() {
        return bankAccountRepo.findAll()
                .stream()
                .map(bankAccountMapper::toDto)
                .toList();
    }

    @Override
    public BankAccountDto getAccountById(Long id) {
        return bankAccountRepo.findById(id)
                .map(bankAccountMapper::toDto)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank account not found with id: " + id));
    }

    @Override
    public void deleteAccount(Long id) {

        BankAccount bankAccount = bankAccountRepo.findById(id)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank account not found with id: " + id));

        ApiResponse<CustomerDto> customerResponse = customerClient.getCustomerById(bankAccount.getCustomerId()).block();
        if (customerResponse == null || !customerResponse.isSuccess() || customerResponse.getData() == null) {
            throw new BankAccountNotFoundException("Customer not found with id: " + id);
        }

        CustomerDto customer = customerResponse.getData();

        try {
            notificationClient.sendNotificationAsync(CreateNotificationDto.builder()
                    .customerId(bankAccount.getCustomerId())
                    .customerEmail(customer.getEmail())
                    .title("Bank Account Closure Notice\n\n")
                    .type("ACCOUNT")
                    .message("Hello " + customer.getUsername()
                            + ", your bank account with number " + bankAccount.getAccountNumber() +
                            " has been successfully closed.\n\nThank you for using our services.")
                    .build());

            log.info("Account closure notification sent successfully for customer id {}", customer.getId());
        } catch (Exception e) {
            log.error("Failed to send account closure notification for customer id {}", customer.getId(), e);
        }

        bankAccountRepo.deleteById(id);

    }

    @Override
    public BankAccountDto updateAccount(Long id, BankAccountDto dto) {
        BankAccount existingAccount = bankAccountRepo.findById(id)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank account not found with id: " + id));

        existingAccount.setBalance(dto.getBalance());
        existingAccount.setAccountType(dto.getAccountType());
        existingAccount.setActive(dto.isActive());

        BankAccount updatedAccount = bankAccountRepo.save(existingAccount);

        ApiResponse<CustomerDto> customerResponse = customerClient.getCustomerById(existingAccount.getCustomerId()).block();
        if (customerResponse == null || !customerResponse.isSuccess() || customerResponse.getData() == null) {
            throw new BankAccountNotFoundException("Customer not found with id: " + dto.getCustomerId());
        }

        CustomerDto customer = customerResponse.getData();

        try {
            notificationClient.sendNotificationAsync(CreateNotificationDto.builder()
                    .customerId(customer.getId())
                    .customerEmail(customer.getEmail())
                    .title("Bank Account Update Notice\n\n")
                    .type("ACCOUNT")
                    .message("Hello " + customer.getUsername()
                            + ", your bank account with number " + existingAccount.getAccountNumber() +
                            " has been updated successfully.\n\n" +
                            "New Balance: " + existingAccount.getBalance())
                    .build());

            log.info("Account update notification sent successfully for customer id {}", customer.getId());
        } catch (Exception e) {
            log.error("Failed to send account update notification for customer id {}", customer.getId(), e);
        }

        return bankAccountMapper.toDto(updatedAccount);
    }

    @Override
    public List<BankAccountDto> getAccountsByCustomerId(Long customerId) {
        return bankAccountRepo.findByCustomerId(customerId)
                .stream()
                .map(bankAccountMapper::toDto)
                .toList();
    }

    @Override
    public List<BankAccountDto> getAccountsByAccountNumber(String accountNumber) {
        return bankAccountRepo.findByAccountNumber(accountNumber)
                .stream()
                .map(bankAccountMapper::toDto)
                .toList();
    }

    @Override
    public List<BankAccountDto> getAccountsByAccountType(String accountType) {
        if (accountType == null || accountType.trim().isEmpty()) {
            throw new InvalidBankAccountOperationException("Account type cannot be null or empty");
        }

        return bankAccountRepo.findByAccountType(AccountType.valueOf(accountType.toUpperCase()))
                .stream()
                .map(bankAccountMapper::toDto)
                .toList();
    }

    @Override
    public BankAccountDto updateAccountBalance(Long id, BigDecimal newBalance) {
        BankAccount bankAccount = bankAccountRepo.findById(id)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank account not found with id: " + id));

        bankAccount.setBalance(newBalance);
        BankAccount updatedAccount = bankAccountRepo.save(bankAccount);

        // Fetch customer info for notification
        ApiResponse<CustomerDto> customerResponse = customerClient.getCustomerById(bankAccount.getCustomerId()).block();
        if (customerResponse == null || customerResponse.getData() == null) {
            throw new BankAccountNotFoundException("Customer not found with id: " + bankAccount.getCustomerId());
        }
        CustomerDto customer = customerResponse.getData();

        try {
            notificationClient.sendNotificationAsync(CreateNotificationDto.builder()
                    .customerId(customer.getId())
                    .customerEmail(customer.getEmail())
                    .title("Bank Account Balance Update Notice\n\n")
                    .type("ACCOUNT")
                    .message("Hello " + customer.getUsername()
                            + ", the balance of your bank account with number " + bankAccount.getAccountNumber() +
                            " has been updated successfully.\n\n" +
                            "New Balance: " + updatedAccount.getBalance())
                    .build());

            log.info("Balance update notification sent successfully for customer id {}", customer.getId());
        } catch (Exception e) {
            log.error("Failed to send balance update notification for customer id {}", customer.getId(), e);
        }

        return bankAccountMapper.toDto(updatedAccount);
    }

    @Override
    public BankAccountDto activateAccount(Long id) {
        BankAccount bankAccount = bankAccountRepo.findById(id)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank account not found with id: " + id));

        bankAccount.setActive(true);
        BankAccount updatedAccount = bankAccountRepo.save(bankAccount);

        sendAccountStatusNotification(updatedAccount, true);

        return bankAccountMapper.toDto(updatedAccount);
    }

    @Override
    public BankAccountDto deactivateAccount(Long id) {
        BankAccount bankAccount = bankAccountRepo.findById(id)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank account not found with id: " + id));

        bankAccount.setActive(false);
        BankAccount updatedAccount = bankAccountRepo.save(bankAccount);

        sendAccountStatusNotification(updatedAccount, false);

        return bankAccountMapper.toDto(updatedAccount);
    }

    private void sendAccountStatusNotification(BankAccount account, boolean activated) {

        ApiResponse<CustomerDto> customerResponse = customerClient
                .getCustomerById(account.getCustomerId())
                .block();

        if (customerResponse == null || customerResponse.getData() == null) {
            log.error("Failed to send notification: Customer not found with id {}", account.getCustomerId());
            return;
        }
        CustomerDto customer = customerResponse.getData();

        String status = activated ? "activated" : "deactivated";
        String title = "Bank Account " + (activated ? "Activation" : "Deactivation") + " Notice\n\n";

        try {
            notificationClient.sendNotificationAsync(CreateNotificationDto.builder()
                    .customerId(customer.getId())
                    .customerEmail(customer.getEmail())
                    .title(title)
                    .type("ACCOUNT")
                    .message("Hello " + customer.getUsername()
                            + ", your bank account with number " + account.getAccountNumber() +
                            " has been " + status + " successfully.")
                    .build());

            log.info("Account {} notification sent successfully for customer id {}", status, customer.getId());
        } catch (Exception e) {
            log.error("Failed to send account {} notification for customer id {}", status, customer.getId(), e);
        }
    }

    private String generateAccountNumber() {
        Year currentYear = Year.now();
        int min = 100000;
        int max = 999999;
        String year = String.valueOf(currentYear);

        String accountNumber;
        int attempts = 0;
        do {
            int random = (int) (Math.random() * (max - min + 1) + min);
            accountNumber = year + random;
            attempts++;
            if (attempts > 5) {
                throw new RuntimeException("Failed to generate unique account number");
            }
        } while (bankAccountRepo.existsByAccountNumber(accountNumber));

        return accountNumber;
    }

}
