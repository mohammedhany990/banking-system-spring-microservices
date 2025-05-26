package com.bankingsystem.loan.helper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.bankingsystem.loan.dto.LoanRepaymentDto;
import com.bankingsystem.loan.dto.LoanRequestDto;
import com.bankingsystem.loan.dto.LoanResponseDto;
import com.bankingsystem.loan.entity.Loan;
import com.bankingsystem.loan.entity.LoanRepayment;

@Mapper(componentModel = "spring")
public interface LoanMapper {

    LoanMapper INSTANCE = Mappers.getMapper(LoanMapper.class);

    // Map LoanRequestDto to Loan entity (ignore id, status, createdAt, dueDate for now)
    Loan loanRequestDtoToLoan(LoanRequestDto dto);

    // Map Loan entity to LoanResponseDto
    LoanResponseDto loanToLoanResponseDto(Loan loan);

    List<LoanResponseDto> loansToLoanResponseDtos(List<Loan> loans);
    
    // Map LoanRepayment entity to LoanRepaymentDto
    LoanRepaymentDto loanRepaymentToLoanRepaymentDto(LoanRepayment entity);
    List<LoanRepaymentDto> loanRepaymentsToLoanRepaymentDtos(List<LoanRepayment> entities);


    // Map LoanRepaymentDto to LoanRepayment entity
    LoanRepayment loanRepaymentDtoToLoanRepayment(LoanRepaymentDto dto);
    
}