package com.bankingsystem.loan.service;

import java.util.List;

import com.bankingsystem.loan.dto.LoanRepaymentDto;
import com.bankingsystem.loan.dto.LoanRequestDto;
import com.bankingsystem.loan.dto.LoanResponseDto;
import com.bankingsystem.loan.entity.Loan;

public interface LoanService {

    // Loan operations
    LoanResponseDto applyLoan(LoanRequestDto loanRequest);

    LoanResponseDto getLoanById(Long loanId);

    List<LoanResponseDto> getLoansByCustomerId(Long customerId);

    LoanResponseDto approveLoan(Long loanId);

    LoanResponseDto rejectLoan(Long loanId);

    LoanResponseDto markLoanAsPaid(Long loanId);

    // Repayment operations
    LoanRepaymentDto makeRepayment(LoanRepaymentDto repaymentRequest);

    List<LoanRepaymentDto> getRepaymentsByLoanId(Long loanId);

    LoanRepaymentDto markRepaymentAsPaid(Long repaymentId);
}
