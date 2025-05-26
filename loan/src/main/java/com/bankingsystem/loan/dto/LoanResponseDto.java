package com.bankingsystem.loan.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanResponseDto {
    private Long id;
    private Long customerId;
    private Long bankAccountId;
    private BigDecimal amount;
    private Integer termInMonths;
    private Double interestRate;
    private String status;
    private LocalDate createdAt;
    private LocalDate dueDate;
}
