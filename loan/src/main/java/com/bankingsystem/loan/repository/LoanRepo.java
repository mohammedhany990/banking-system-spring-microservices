package com.bankingsystem.loan.repository;


import com.bankingsystem.loan.entity.Loan;
import com.bankingsystem.loan.entity.LoanStatus;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepo extends JpaRepository<Loan, Long> {
    List<Loan> findByCustomerId(Long customerId);
    List<Loan> findByStatus(LoanStatus status);
}