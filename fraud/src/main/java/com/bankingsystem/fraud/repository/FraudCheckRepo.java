package com.bankingsystem.fraud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bankingsystem.fraud.entity.FraudCheckHistory;

public interface FraudCheckRepo extends JpaRepository<FraudCheckHistory, Long> {
   
}
