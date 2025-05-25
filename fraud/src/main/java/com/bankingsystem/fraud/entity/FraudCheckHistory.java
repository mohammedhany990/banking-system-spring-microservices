package com.bankingsystem.fraud.entity;


import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fraudCheckHistory")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FraudCheckHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fraud_check_history_seq")
    @SequenceGenerator(name = "fraud_check_history_seq", sequenceName = "fraud_check_history_id_seq", allocationSize = 1)
    private Long id;

    private Long customerId;

    @Column(nullable = false)
    private boolean isFraudster;


    private LocalDateTime createdAt;
}
