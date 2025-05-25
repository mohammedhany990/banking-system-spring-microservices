package com.bankingsystem.bankaccount.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bankingsystem.bankaccount.client.CustomerClient;
import com.bankingsystem.bankaccount.dto.BankAccountDto;
import com.bankingsystem.bankaccount.dto.CustomerDto;
import com.bankingsystem.bankaccount.entity.AccountType;
import com.bankingsystem.bankaccount.entity.BankAccount;
import com.bankingsystem.bankaccount.exception.BankAccountNotFoundException;
import com.bankingsystem.bankaccount.helper.BankAccountMapper;
import com.bankingsystem.bankaccount.repository.BankAccountRepo;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepo bankAccountRepo;
    private final BankAccountMapper bankAccountMapper;
    private final CustomerClient customerClient;

    @Override
    public BankAccountDto createAccount(BankAccountDto dto) {

        CustomerDto customer = customerClient.getCustomerById(dto.getCustomerId());
        if (customer == null) {
            throw new BankAccountNotFoundException("Customer not found with id: " + dto.getCustomerId());
        }
        BankAccount bankAccount = bankAccountMapper.toEntity(dto);

        BankAccount savedAccount = bankAccountRepo.save(bankAccount);

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
        if (!bankAccountRepo.existsById(id)) {
            throw new BankAccountNotFoundException("Bank account not found with id: " + id);
        }
        bankAccountRepo.deleteById(id);

    }

    @Override
    public BankAccountDto updateAccount(Long id, BankAccountDto dto) {
        if (!bankAccountRepo.existsById(id)) {
            throw new BankAccountNotFoundException("Bank account not found with id: " + id);
        }

        BankAccount bankAccount = bankAccountMapper.toEntity(dto);
        bankAccount.setId(id);

        BankAccount updatedAccount = bankAccountRepo.save(bankAccount);

        return bankAccountMapper.toDto(updatedAccount);
    }

    @Override
    public BankAccountDto activateAccount(Long id) {
        BankAccount bankAccount = bankAccountRepo.findById(id)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank account not found with id: " + id));

        bankAccount.setActive(true);
        BankAccount updatedAccount = bankAccountRepo.save(bankAccount);

        return bankAccountMapper.toDto(updatedAccount);
    }

    @Override
    public BankAccountDto deactivateAccount(Long id) {
        BankAccount bankAccount = bankAccountRepo.findById(id)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank account not found with id: " + id));

        bankAccount.setActive(false);
        BankAccount updatedAccount = bankAccountRepo.save(bankAccount);

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
            throw new IllegalArgumentException("Account type cannot be null or empty");
        }
    
        return bankAccountRepo.findByAccountType(AccountType.valueOf(accountType.toUpperCase()))
                .stream()
                .map(bankAccountMapper::toDto)
                .toList();
    }

}
