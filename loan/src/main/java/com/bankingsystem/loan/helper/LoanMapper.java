package com.bankingsystem.loan.helper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.bankingsystem.loan.dto.LoanRepaymentDto;
import com.bankingsystem.loan.dto.LoanRequestDto;
import com.bankingsystem.loan.dto.LoanResponseDto;
import com.bankingsystem.loan.dto.RepaymentScheduleDto;
import com.bankingsystem.loan.entity.Loan;
import com.bankingsystem.loan.entity.LoanRepayment;

@Mapper(componentModel = "spring")
public interface LoanMapper {

    LoanMapper INSTANCE = Mappers.getMapper(LoanMapper.class);

    Loan loanRequestDtoToLoan(LoanRequestDto dto);

    LoanResponseDto loanToLoanResponseDto(Loan loan);

    List<LoanResponseDto> loansToLoanResponseDtos(List<Loan> loans);
    
    LoanRepaymentDto loanRepaymentToLoanRepaymentDto(LoanRepayment entity);
    
    List<LoanRepaymentDto> loanRepaymentsToLoanRepaymentDtos(List<LoanRepayment> entities);


    LoanRepayment loanRepaymentDtoToLoanRepayment(LoanRepaymentDto dto);

    RepaymentScheduleDto loanRepaymentToRepaymentScheduleDto(LoanRepayment entity);
    
    List<RepaymentScheduleDto> loanRepaymentsToRepaymentScheduleDtos(List<LoanRepayment> entities);
    
}