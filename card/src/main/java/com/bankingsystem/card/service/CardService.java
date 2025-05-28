package com.bankingsystem.card.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.bankingsystem.card.dto.CardRequestDto;
import com.bankingsystem.card.dto.CardResponse;
import com.bankingsystem.card.dto.TransactionResponse;
import com.bankingsystem.card.dto.transactions.DepositRequest;
import com.bankingsystem.card.dto.transactions.TransferRequest;
import com.bankingsystem.card.dto.transactions.WithdrawRequest;

public interface CardService {

    CardResponse createCard(CardRequestDto cardRequestDto ) ;

    CardResponse getCardById(Long cardId);

    List<CardResponse> getCardsByAccountId(Long accountId);

    void deactivateCard(Long cardId);

    void blockCard(Long cardId);

    void deleteCard(Long cardId);

    boolean validateCardNumber(String cardNumber);

    CardResponse updateCardStatus(Long cardId, String status);

    CardResponse regenerateCard(Long oldCardId);

    boolean isCardExpired(Long cardId);



    TransactionResponse withdrawUsingCard(Long cardId,WithdrawRequest request);
    TransactionResponse depositUsingCard(Long cardId,DepositRequest request);
    //TransactionResponse transferUsingCard(Long cardId,TransferRequest request);

}