package com.bankingsystem.card.dto;

import java.time.LocalDate;

import com.bankingsystem.card.entity.CardNetwork;
import com.bankingsystem.card.entity.CardStatus;
import com.bankingsystem.card.entity.CardType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CardResponse {
 private Long id;
    private String cardNumber;
    private String cardHolderName;
    private LocalDate expiryDate;
    private CardNetwork cardNetwork;
    private CardType cardType;
    private CardStatus status;
    private Long accountId;
}
