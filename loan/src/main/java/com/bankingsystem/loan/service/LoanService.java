package com.bankingsystem.loan.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bankingsystem.loan.client.AccountClient;
import com.bankingsystem.loan.client.CustomerClient;
import com.bankingsystem.loan.dto.BankAccountDto;
import com.bankingsystem.loan.dto.CustomerDto;
import com.bankingsystem.loan.dto.LoanRepaymentDto;
import com.bankingsystem.loan.dto.LoanRequestDto;
import com.bankingsystem.loan.dto.LoanResponseDto;
import com.bankingsystem.loan.dto.RepaymentScheduleDto;
import com.bankingsystem.loan.entity.Loan;
import com.bankingsystem.loan.entity.LoanRepayment;
import com.bankingsystem.loan.entity.LoanStatus;
import com.bankingsystem.loan.exception.LoanValidationException;
import com.bankingsystem.loan.helper.ApiResponse;
import com.bankingsystem.loan.helper.LoanMapper;
import com.bankingsystem.loan.repository.LoanRepaymentRepo;
import com.bankingsystem.loan.repository.LoanRepo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class LoanService  {

    private final LoanRepo loanRepo;

    private final LoanRepaymentRepo loanRepaymentRepo;

    private final AccountClient accountClient;
    private final CustomerClient customerClient;
    private final LoanMapper loanMapper;

    public LoanResponseDto applyLoan(LoanRequestDto loanRequest) {
        validateLoanRequest(loanRequest);

        ApiResponse<CustomerDto> customerResponse = customerClient.getCustomerById(loanRequest.getCustomerId());
        if (customerResponse == null || !customerResponse.isSuccess() || customerResponse.getData() == null) {
            throw new LoanValidationException("Customer not found with id: " + loanRequest.getCustomerId());
        }

        ApiResponse<BankAccountDto> accountResponse = accountClient.getAccountById(loanRequest.getBankAccountId());
        if (accountResponse == null || !accountResponse.isSuccess() || accountResponse.getData() == null) {
            throw new LoanValidationException("Bank account not found with id: " + loanRequest.getBankAccountId());
        }

        if (!accountResponse.getData().getCustomerId().equals(loanRequest.getCustomerId())) {
            throw new LoanValidationException("Bank account " + loanRequest.getBankAccountId()
                    + " does not belong to customer " + loanRequest.getCustomerId());
        }

        Loan loan = loanMapper.loanRequestDtoToLoan(loanRequest);
        loan.setStatus(LoanStatus.PENDING);
        loan.setCreatedAt(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusMonths(loanRequest.getTermInMonths()));

        Loan savedLoan = loanRepo.save(loan);

        return loanMapper.loanToLoanResponseDto(savedLoan);
    }

    private void validateLoanRequest(LoanRequestDto loanRequest) {
        if (loanRequest.getAmount() == null || loanRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new LoanValidationException("Loan amount must be greater than zero");
        }
        if (loanRequest.getTermInMonths() == null || loanRequest.getTermInMonths() <= 0) {
            throw new LoanValidationException("Loan term must be greater than zero months");
        }
        if (loanRequest.getInterestRate() == null || loanRequest.getInterestRate() < 0) {
            throw new LoanValidationException("Interest rate must be non-negative");
        }
    }

    public LoanResponseDto getLoanById(Long loanId) {
        Loan loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new LoanValidationException("Loan not found with id: " + loanId));
        return loanMapper.loanToLoanResponseDto(loan);
    }

    public List<LoanResponseDto> getLoansByCustomerId(Long customerId) {
        List<Loan> loans = loanRepo.findByCustomerId(customerId);
        if (loans.isEmpty()) {
            throw new LoanValidationException("No loans found for customer with id: " + customerId);
        }
        return loanMapper.loansToLoanResponseDtos(loans);
    }

    public LoanResponseDto approveLoan(Long loanId) {
        Loan loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new LoanValidationException("Loan not found with id: " + loanId));

        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new LoanValidationException("Loan with id " + loanId + " is not in PENDING status");
        }

        loan.setStatus(LoanStatus.APPROVED);
        Loan updatedLoan = loanRepo.save(loan);

        return loanMapper.loanToLoanResponseDto(updatedLoan);
    }

    public LoanResponseDto rejectLoan(Long loanId) {
        Loan loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new LoanValidationException("Loan not found with id: " + loanId));

        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new LoanValidationException("Loan with id " + loanId + " is not in PENDING status");
        }

        loan.setStatus(LoanStatus.REJECTED);
        Loan updatedLoan = loanRepo.save(loan);

        return loanMapper.loanToLoanResponseDto(updatedLoan);
    }

    public LoanResponseDto markLoanAsPaid(Long loanId) {
        Loan loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new LoanValidationException("Loan not found with id: " + loanId));

        if (loan.getStatus() != LoanStatus.APPROVED) {
            throw new LoanValidationException("Loan with id " + loanId + " is not in APPROVED status");
        }

        loan.setStatus(LoanStatus.PAID);
        Loan updatedLoan = loanRepo.save(loan);

        return loanMapper.loanToLoanResponseDto(updatedLoan);
    }

    public LoanRepaymentDto makeRepayment(LoanRepaymentDto repaymentRequest) {

        if (repaymentRequest.getLoanId() == null || repaymentRequest.getAmount() == null
                || repaymentRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new LoanValidationException("Invalid repayment request");
        }

        Loan loan = loanRepo.findById(repaymentRequest.getLoanId())
                .orElseThrow(
                        () -> new LoanValidationException("Loan not found with id: " + repaymentRequest.getLoanId()));

        if (loan.getStatus() != LoanStatus.APPROVED && loan.getStatus() != LoanStatus.PENDING
                && loan.getStatus() != LoanStatus.ACTIVE) {
            throw new LoanValidationException(
                    "Loan with id " + repaymentRequest.getLoanId() + " is not in a valid state for repayments");
        }

        LoanRepayment repayment = loanMapper.loanRepaymentDtoToLoanRepayment(repaymentRequest);

        // Set loanId explicitly (since no relation)
        repayment.setLoanId(loan.getId());

        repayment.setIsPaid(false); // Initially not paid
        repayment.setCreatedAt(LocalDate.now());

        LoanRepayment savedRepayment = loanRepaymentRepo.save(repayment);

        return loanMapper.loanRepaymentToLoanRepaymentDto(savedRepayment);
    }

    public List<LoanRepaymentDto> getRepaymentsByLoanId(Long loanId) {
        List<LoanRepayment> repayments = loanRepaymentRepo.findByLoanId(loanId);
        if (repayments.isEmpty()) {
            throw new LoanValidationException("No repayments found for loan with id: " + loanId);
        }
        return repayments.stream()
                .map(loanMapper::loanRepaymentToLoanRepaymentDto)
                .toList();
    }

    public LoanRepaymentDto markRepaymentAsPaid(Long repaymentId) {

        LoanRepayment repayment = loanRepaymentRepo.findById(repaymentId)
                .orElseThrow(() -> new LoanValidationException("Repayment not found with id: " + repaymentId));

        if (repayment.getIsPaid()) {
            throw new LoanValidationException("Repayment with id " + repaymentId + " is already marked as paid");
        }

        repayment.setIsPaid(true);
        LoanRepayment updatedRepayment = loanRepaymentRepo.save(repayment);
        return loanMapper.loanRepaymentToLoanRepaymentDto(updatedRepayment);
    }

    public List<LoanResponseDto> getAllLoans() {
        List<Loan> loans = loanRepo.findAll();
        if (loans.isEmpty()) {
            throw new LoanValidationException("No loans found");
        }
        return loanMapper.loansToLoanResponseDtos(loans);
    }

    public LoanResponseDto cancelLoan(Long loanId) {
        Loan loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new LoanValidationException("Loan not found with id: " + loanId));

        if (loan.getStatus() != LoanStatus.PENDING && loan.getStatus() != LoanStatus.APPROVED) {
            throw new LoanValidationException("Loan with id " + loanId + " cannot be cancelled in its current state");
        }

        loan.setStatus(LoanStatus.CANCELLED);
        Loan updatedLoan = loanRepo.save(loan);

        return loanMapper.loanToLoanResponseDto(updatedLoan);
    }

    public List<RepaymentScheduleDto> getRepaymentSchedule(Long loanId) {
        Loan loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new LoanValidationException("Loan not found with id: " + loanId));

        List<LoanRepayment> repayments = loanRepaymentRepo.findByLoanId(loanId);
        if (repayments.isEmpty()) {
            throw new LoanValidationException("No repayments found for loan with id: " + loanId);
        }

        return loanMapper.loanRepaymentsToRepaymentScheduleDtos(repayments);
    }

    public List<LoanResponseDto> getLoansByStatus(String status) {
        LoanStatus loanStatus;
        try {
            loanStatus = LoanStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new LoanValidationException("Invalid loan status: " + status);
        }
        List<Loan> loans = loanRepo.findByStatus(loanStatus);

        if (loans.isEmpty()) {
            throw new LoanValidationException("No loans found with status: " + status);
        }
        return loanMapper.loansToLoanResponseDtos(loans);
    }

}
