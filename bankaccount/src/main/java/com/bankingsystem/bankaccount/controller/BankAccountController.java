package com.bankingsystem.bankaccount.controller;

import java.util.List;

import com.bankingsystem.bankaccount.service.BankAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bankingsystem.bankaccount.dto.BankAccountDto;
import com.bankingsystem.bankaccount.dto.CreateBankAccountDto;
import com.bankingsystem.bankaccount.dto.UpdateBalanceRequest;
import com.bankingsystem.bankaccount.helper.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/bank-accounts")
@RequiredArgsConstructor
public class BankAccountController {

    private final BankAccountService bankAccountService;

    @PostMapping
    public ResponseEntity<ApiResponse<BankAccountDto>> createAccount(@Valid @RequestBody CreateBankAccountDto bankAccountDto) {
        BankAccountDto createdAccount = bankAccountService.createAccount(bankAccountDto);
        ApiResponse<BankAccountDto> response = ApiResponse.<BankAccountDto>builder()
                .success(true)
                .message("Bank account created successfully")
                .data(createdAccount)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BankAccountDto>>> getAllAccounts() {
        List<BankAccountDto> accounts = bankAccountService.getAllAccounts();
        ApiResponse<List<BankAccountDto>> response = ApiResponse.<List<BankAccountDto>>builder()
                .success(true)
                .message("List of bank accounts")
                .data(accounts)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BankAccountDto>> getAccountById(@PathVariable Long id) {
        BankAccountDto account = bankAccountService.getAccountById(id);
        ApiResponse<BankAccountDto> response = ApiResponse.<BankAccountDto>builder()
                .success(true)
                .message("Bank account retrieved successfully")
                .data(account)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(@PathVariable Long id) {
        bankAccountService.deleteAccount(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Bank account deleted successfully")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BankAccountDto>> updateAccount(@PathVariable Long id,
            @RequestBody BankAccountDto bankAccountDto) {
        BankAccountDto updatedAccount = bankAccountService.updateAccount(id, bankAccountDto);
        ApiResponse<BankAccountDto> response = ApiResponse.<BankAccountDto>builder()
                .success(true)
                .message("Bank account updated successfully")
                .data(updatedAccount)
                .build();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<BankAccountDto>> activateAccount(@PathVariable Long id) {
        BankAccountDto activatedAccount = bankAccountService.activateAccount(id);
        ApiResponse<BankAccountDto> response = ApiResponse.<BankAccountDto>builder()
                .success(true)
                .message("Bank account activated successfully")
                .data(activatedAccount)
                .build();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<BankAccountDto>> deactivateAccount(@PathVariable Long id) {

        BankAccountDto deactivatedAccount = bankAccountService.deactivateAccount(id);

        ApiResponse<BankAccountDto> response = ApiResponse.<BankAccountDto>builder()
                .success(true)
                .message("Bank account deactivated successfully")
                .data(deactivatedAccount)
                .build();
                
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<BankAccountDto>>> getAccountsByCustomerId(@PathVariable Long customerId) {
        List<BankAccountDto> accounts = bankAccountService.getAccountsByCustomerId(customerId);
        ApiResponse<List<BankAccountDto>> response = ApiResponse.<List<BankAccountDto>>builder()
                .success(true)
                .message("List of bank accounts for customer")
                .data(accounts)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/account-number/{accountNumber}")
    public ResponseEntity<ApiResponse<List<BankAccountDto>>> getAccountsByAccountNumber(
            @PathVariable String accountNumber) {
        List<BankAccountDto> accounts = bankAccountService.getAccountsByAccountNumber(accountNumber);
        ApiResponse<List<BankAccountDto>> response = ApiResponse.<List<BankAccountDto>>builder()
                .success(true)
                .message("List of bank accounts by account number")
                .data(accounts)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/account-type/{accountType}")
    public ResponseEntity<ApiResponse<List<BankAccountDto>>> getAccountsByAccountType(
            @PathVariable String accountType) {

        List<BankAccountDto> accounts = bankAccountService.getAccountsByAccountType(accountType);
        ApiResponse<List<BankAccountDto>> response = ApiResponse.<List<BankAccountDto>>builder()
                .success(true)
                .message("List of bank accounts by account type")
                .data(accounts)
                .build();
        return ResponseEntity.ok(response);
    }

   
    @PutMapping("/{id}/balance")
    public ResponseEntity<ApiResponse<BankAccountDto>> updateBalance(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBalanceRequest request) {

        BankAccountDto updatedAccount = bankAccountService.updateAccountBalance(id, request.getNewBalance());

        ApiResponse<BankAccountDto> response = ApiResponse.<BankAccountDto>builder()
                .success(true)
                .message("Bank account balance updated successfully")
                .data(updatedAccount)
                .build();

        return ResponseEntity.ok(response);
    }

}
