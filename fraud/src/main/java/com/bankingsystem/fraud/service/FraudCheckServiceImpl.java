package com.bankingsystem.fraud.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.bankingsystem.fraud.entity.FraudCheckHistory;
import com.bankingsystem.fraud.repository.FraudCheckRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FraudCheckServiceImpl implements FraudCheckService {

    private final FraudCheckRepo fraudCheckRepo;

    @Override
    public boolean isFraudulentCustomer(Long customerId) {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }
        if (fraudCheckRepo.existsById(customerId)) {
            return true;
        }

        fraudCheckRepo.save(FraudCheckHistory.builder()
                .customerId(customerId)
                .createdAt(LocalDateTime.now())
                .build());

        return false;
    }

}
