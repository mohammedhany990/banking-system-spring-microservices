package com.bankingsystem.loan.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bankingsystem.loan.dto.LoanRepaymentDto;
import com.bankingsystem.loan.dto.LoanRequestDto;
import com.bankingsystem.loan.dto.LoanResponseDto;
import com.bankingsystem.loan.service.LoanService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping("/apply")
    public ResponseEntity<LoanResponseDto> applyLoan(@RequestBody LoanRequestDto loanRequest) {
        LoanResponseDto response = loanService.applyLoan(loanRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{loanId}")
    public ResponseEntity<LoanResponseDto> getLoanById(@PathVariable Long loanId) {
        LoanResponseDto response = loanService.getLoanById(loanId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<LoanResponseDto>> getLoansByCustomerId(@PathVariable Long customerId) {
        List<LoanResponseDto> response = loanService.getLoansByCustomerId(customerId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{loanId}/approve")
    public ResponseEntity<LoanResponseDto> approveLoan(@PathVariable Long loanId) {
        LoanResponseDto response = loanService.approveLoan(loanId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{loanId}/reject")
    public ResponseEntity<LoanResponseDto> rejectLoan(@PathVariable Long loanId) {
        LoanResponseDto response = loanService.rejectLoan(loanId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{loanId}/paid")
    public ResponseEntity<LoanResponseDto> markLoanAsPaid(@PathVariable Long loanId) {
        LoanResponseDto response = loanService.markLoanAsPaid(loanId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/repayments")
    public ResponseEntity<LoanRepaymentDto> makeRepayment(@RequestBody LoanRepaymentDto repaymentRequest) {
        LoanRepaymentDto response = loanService.makeRepayment(repaymentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{loanId}/repayments")
    public ResponseEntity<List<LoanRepaymentDto>> getRepaymentsByLoanId(@PathVariable Long loanId) {
        List<LoanRepaymentDto> response = loanService.getRepaymentsByLoanId(loanId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/repayments/{repaymentId}/paid")
    public ResponseEntity<LoanRepaymentDto> markRepaymentAsPaid(@PathVariable Long repaymentId) {
        LoanRepaymentDto response = loanService.markRepaymentAsPaid(repaymentId);
        return ResponseEntity.ok(response);
    }
}
