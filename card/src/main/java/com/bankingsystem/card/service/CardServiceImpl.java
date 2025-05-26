package com.bankingsystem.card.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bankingsystem.card.client.AccountClient;
import com.bankingsystem.card.client.CustomerClient;
import com.bankingsystem.card.client.TransactionClient;
import com.bankingsystem.card.dto.BankAccountDto;
import com.bankingsystem.card.dto.CardRequestDto;
import com.bankingsystem.card.dto.CardResponse;
import com.bankingsystem.card.dto.TransactionResponse;
import com.bankingsystem.card.dto.transactions.CustomerDto;
import com.bankingsystem.card.dto.transactions.DepositRequest;
import com.bankingsystem.card.dto.transactions.TransferRequest;
import com.bankingsystem.card.dto.transactions.WithdrawRequest;
import com.bankingsystem.card.entity.Card;
import com.bankingsystem.card.entity.CardStatus;
import com.bankingsystem.card.exception.BankAccountNotFoundException;
import com.bankingsystem.card.exception.CardExpiredException;
import com.bankingsystem.card.exception.CardNotActiveException;
import com.bankingsystem.card.exception.CardNotFoundException;
import com.bankingsystem.card.exception.InsufficientBalanceException;
import com.bankingsystem.card.exception.SameAccountTransferException;
import com.bankingsystem.card.exception.TransferFailedException;
import com.bankingsystem.card.helper.ApiResponse;
import com.bankingsystem.card.helper.CardMapper;
import com.bankingsystem.card.helper.CardUtils;
import com.bankingsystem.card.repository.CardRepo;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CardServiceImpl implements CardService {

    private final CardRepo cardRepo;
    private final AccountClient accountClient;
    private final CustomerClient customerClient;
    private final CardMapper cardMapper;
    private final TransactionClient transactionClient;

    @Override
    public CardResponse createCard(CardRequestDto cardRequestDto) {

        ApiResponse<BankAccountDto> bankAccountResponse = accountClient.getAccountById(cardRequestDto.getAccountId());

        if (bankAccountResponse == null || bankAccountResponse.getData() == null) {
            throw new BankAccountNotFoundException(
                    "Bank account with ID " + cardRequestDto.getAccountId() + " not found.");
        }

        BankAccountDto bankAccount = bankAccountResponse.getData();

        ApiResponse<CustomerDto> customerResponse = customerClient.getCustomerById(bankAccount.getCustomerId());

        if (customerResponse == null || customerResponse.getData() == null) {
            throw new BankAccountNotFoundException("Customer with ID " + bankAccount.getCustomerId() + " not found.");
        }

        CustomerDto customer = customerResponse.getData();

        Card card = CardUtils.generateCard(
                cardRequestDto.getAccountId(),
                cardRequestDto.getCardType(),
                cardRequestDto.getCardNetwork(),
                customer.getFirstName() + "_" + customer.getLastName());

        Card savedCard = cardRepo.save(card);

        return cardMapper.toCardResponse(savedCard);
    }

    @Override
    public CardResponse getCardById(Long cardId) {

        Card card = cardRepo.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card with ID " + cardId + " not found."));

        return cardMapper.toCardResponse(card);
    }

    @Override
    public List<CardResponse> getCardsByAccountId(Long accountId) {
        List<Card> cards = cardRepo.findByAccountId(accountId);
        return cardMapper.toCardResponseList(cards);
    }

    @Override
    public void deactivateCard(Long cardId) {

        Card card = cardRepo.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card with ID " + cardId + " not found."));

        card.setStatus(CardStatus.INACTIVE);

        cardRepo.save(card);
    }

    @Override
    public void blockCard(Long cardId) {
        Card card = cardRepo.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card with ID " + cardId + " not found."));

        card.setStatus(CardStatus.BLOCKED);

        cardRepo.save(card);
    }

    @Override
    public void deleteCard(Long cardId) {
        Card card = cardRepo.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card with ID " + cardId + " not found."));
        cardRepo.delete(card);
    }

    @Override
    public boolean validateCardNumber(String cardNumber) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CardResponse updateCardStatus(Long cardId, String status) {
        Card card = cardRepo.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card with ID " + cardId + " not found."));

        CardStatus newStatus;
        try {
            newStatus = CardStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid card status: " + status);
        }

        card.setStatus(newStatus);
        Card updatedCard = cardRepo.save(card);

        return cardMapper.toCardResponse(updatedCard);
    }

    @Override
    public CardResponse regenerateCard(Long oldCardId) {

        Card oldCard = cardRepo.findById(oldCardId)
                .orElseThrow(() -> new CardNotFoundException("Card with ID " + oldCardId + " not found."));

        Card newCard = CardUtils.generateCard(
                oldCard.getAccountId(),
                oldCard.getCardType(),
                oldCard.getCardNetwork(),
                oldCard.getCardHolderName());

        Card savedNewCard = cardRepo.save(newCard);
        oldCard.setStatus(CardStatus.INACTIVE);
        cardRepo.save(oldCard);

        return cardMapper.toCardResponse(savedNewCard);
    }

    @Override
    public boolean isCardExpired(Long cardId) {
        Card card = cardRepo.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card with ID " + cardId + " not found."));

        LocalDate today = LocalDate.now();
        return card.getExpiryDate() != null
                && (card.getExpiryDate().isBefore(today) || card.getExpiryDate().isEqual(today));
    }

    @Override
    public TransactionResponse withdrawUsingCard(Long cardId, WithdrawRequest request) {

        Card card = cardRepo.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card with ID " + cardId + " not found."));

        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new CardNotActiveException("Card is not active.");
        }

        if (card.getExpiryDate() != null && card.getExpiryDate().isBefore(LocalDate.now())) {
            throw new CardExpiredException("Card is expired.");
        }

        ApiResponse<BankAccountDto> accountResponse = accountClient.getAccountById(card.getAccountId());
        if (accountResponse == null || accountResponse.getData() == null) {
            throw new BankAccountNotFoundException("Bank account not found.");
        }
        BankAccountDto bankAccount = accountResponse.getData();

        BigDecimal amount = request.getAmount();
        if (bankAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance.");
        }

        ApiResponse<TransactionResponse> withdrawResponse = transactionClient.withdraw(request);

        if (withdrawResponse == null || !withdrawResponse.isSuccess()) {
            throw new IllegalStateException("Failed to withdraw amount.");
        }

        return withdrawResponse.getData();
    }

    @Override
    public TransactionResponse depositUsingCard(Long cardId, DepositRequest request) {

        Card card = cardRepo.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card with ID " + cardId + " not found."));

        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new CardNotActiveException("Card is not active.");
        }

        if (card.getExpiryDate() != null && card.getExpiryDate().isBefore(LocalDate.now())) {
            throw new CardExpiredException("Card is expired.");
        }

        ApiResponse<BankAccountDto> accountResponse = accountClient.getAccountById(card.getAccountId());
        if (accountResponse == null || accountResponse.getData() == null) {
            throw new BankAccountNotFoundException("Bank account not found.");
        }

        BigDecimal amount = request.getAmount();
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero.");
        }

        ApiResponse<TransactionResponse> depositResponse = transactionClient.deposit(request);

        if (depositResponse == null || !depositResponse.isSuccess()) {
            throw new IllegalStateException("Failed to deposit amount.");
        }

        return depositResponse.getData();
    }

    @Override
    public TransactionResponse transferUsingCard(Long cardId, TransferRequest request) {
        Card card = cardRepo.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card with ID " + cardId + " not found."));

        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new CardNotActiveException("Card is not active.");
        }

        if (card.getExpiryDate() != null && card.getExpiryDate().isBefore(LocalDate.now())) {
            throw new CardExpiredException("Card is expired.");
        }

        ApiResponse<BankAccountDto> senderAccountResponse = accountClient.getAccountById(card.getAccountId());
        if (senderAccountResponse == null || senderAccountResponse.getData() == null) {
            throw new BankAccountNotFoundException("Sender bank account not found.");
        }
        BankAccountDto senderAccount = senderAccountResponse.getData();

        BigDecimal amount = request.getAmount();
        if (senderAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance.");
        }

        ApiResponse<BankAccountDto> receiverAccountResponse = accountClient
                .getAccountById(request.getToAccountId());
        if (receiverAccountResponse == null || receiverAccountResponse.getData() == null) {
            throw new BankAccountNotFoundException("Receiver bank account not found.");
        }

        if (senderAccount.getId().equals(request.getToAccountId())) {
            throw new SameAccountTransferException("Sender and receiver accounts cannot be the same.");
        }

        ApiResponse<TransactionResponse> transferResponse = transactionClient.transfer(request);

        if (transferResponse == null || !transferResponse.isSuccess()) {
            throw new TransferFailedException("Failed to transfer amount.");
        }

        return transferResponse.getData();
    }

}
