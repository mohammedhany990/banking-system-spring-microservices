package com.bankingsystem.fraud.controller;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bankingsystem.fraud.dto.FraudCheckResponse;
import com.bankingsystem.fraud.service.FraudCheckService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/fraud")
@RequiredArgsConstructor
public class FraudController {
    private final FraudCheckService fraudCheckService;

    @GetMapping("/fraud-check/{customerId}")
    public FraudCheckResponse isFraudulentCustomer(@PathVariable("customerId") Long customerId) {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }

        boolean isFraudster = fraudCheckService.isFraudulentCustomer(customerId);
        return FraudCheckResponse.builder()
                .isFraudster(isFraudster)
                .build();
    }

    @GetMapping
    public String healthCheck() {
        return "Fraud service is up and running!";
    }

}
