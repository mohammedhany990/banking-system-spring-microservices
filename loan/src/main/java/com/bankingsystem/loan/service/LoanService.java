package com.bankingsystem.loan.service;

import java.util.List;

import com.bankingsystem.loan.dto.LoanRepaymentDto;
import com.bankingsystem.loan.dto.LoanRequestDto;
import com.bankingsystem.loan.dto.LoanResponseDto;
import com.bankingsystem.loan.dto.RepaymentScheduleDto;
import com.bankingsystem.loan.entity.Loan;
import com.bankingsystem.loan.entity.LoanStatus;

public interface LoanService {

    LoanResponseDto applyLoan(LoanRequestDto loanRequest);

    LoanResponseDto getLoanById(Long loanId);

    List<LoanResponseDto> getLoansByCustomerId(Long customerId);

    List<LoanResponseDto> getAllLoans();

    LoanResponseDto approveLoan(Long loanId);

    LoanResponseDto rejectLoan(Long loanId);

    LoanResponseDto markLoanAsPaid(Long loanId);

    LoanResponseDto cancelLoan(Long loanId);

    LoanRepaymentDto makeRepayment(LoanRepaymentDto repaymentRequest);

    List<LoanRepaymentDto> getRepaymentsByLoanId(Long loanId);

    LoanRepaymentDto markRepaymentAsPaid(Long repaymentId);

    List<RepaymentScheduleDto> getRepaymentSchedule(Long loanId);

    List<LoanResponseDto> getLoansByStatus(String status);
}
