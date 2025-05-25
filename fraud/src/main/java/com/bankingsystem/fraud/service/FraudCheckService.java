package com.bankingsystem.fraud.service;

public interface FraudCheckService {

    boolean isFraudulentCustomer(Long customerId);
}
