package com.bankingsystem.loan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanRequestDto {
    private Long customerId;
    private Long bankAccountId;
    private BigDecimal amount;
    private Integer termInMonths;
    private Double interestRate;
}