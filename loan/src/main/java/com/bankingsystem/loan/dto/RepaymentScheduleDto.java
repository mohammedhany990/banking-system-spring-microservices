package com.bankingsystem.loan.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepaymentScheduleDto {

    private Long repaymentId;

    private Long loanId;

    private LocalDate dueDate;

    private BigDecimal amountDue;

    private RepaymentStatus status;

    private LocalDate createdAt;

}
