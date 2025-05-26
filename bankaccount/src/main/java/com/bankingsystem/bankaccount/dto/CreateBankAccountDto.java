package com.bankingsystem.bankaccount.dto;

import java.math.BigDecimal;

import com.bankingsystem.bankaccount.entity.AccountType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBankAccountDto {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Balance is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Balance cannot be negative")
    private BigDecimal balance;

    @NotNull(message = "Account type is required")
    private AccountType accountType;

    private boolean active;
}
