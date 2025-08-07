package com.bankingsystem.card.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bankingsystem.card.client.AccountClient;
import com.bankingsystem.card.client.CustomerClient;
import com.bankingsystem.card.client.NotificationClient;
import com.bankingsystem.card.client.TransactionClient;
import com.bankingsystem.card.dto.BankAccountDto;
import com.bankingsystem.card.dto.CardRequestDto;
import com.bankingsystem.card.dto.CardResponse;
import com.bankingsystem.card.dto.CreateNotificationDto;
import com.bankingsystem.card.dto.TransactionResponse;
import com.bankingsystem.card.dto.transactions.CustomerDto;
import com.bankingsystem.card.dto.transactions.DepositRequest;
import com.bankingsystem.card.dto.transactions.WithdrawRequest;
import com.bankingsystem.card.entity.Card;
import com.bankingsystem.card.entity.CardStatus;
import com.bankingsystem.card.exception.BankAccountNotFoundException;
import com.bankingsystem.card.exception.CardExpiredException;
import com.bankingsystem.card.exception.CardNotActiveException;
import com.bankingsystem.card.exception.CardNotFoundException;
import com.bankingsystem.card.exception.InsufficientBalanceException;
import com.bankingsystem.card.helper.ApiResponse;
import com.bankingsystem.card.helper.CardMapper;
import com.bankingsystem.card.helper.CardUtils;
import com.bankingsystem.card.repository.CardRepo;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CardServiceImpl implements CardService {

    private final CardRepo cardRepo;
    private final AccountClient accountClient;
    private final CustomerClient customerClient;
    private final CardMapper cardMapper;
    private final TransactionClient transactionClient;
    private final NotificationClient notificationClient;

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

        try {

            notificationClient.createNotification(CreateNotificationDto.builder()
                    .customerId(customer.getId())
                    .customerEmail(customer.getEmail())
                    .title("New Card Created")
                    .type("CARD")
                    .message("Dear " + customer.getFirstName() + ", your new card with number "
                            + savedCard.getCardNumber() + " has been successfully created.")
                    .build());
        } catch (Exception e) {
            log.error("Failed to send notification for card creation for customer id {}", customer.getId(), e);
        }

        return cardMapper.toCardResponse(savedCard);
    }

    @Override
    public CardResponse getCardById(Long cardId) {

        return cardRepo.findById(cardId)
                .map(cardMapper::toCardResponse)
                .orElseThrow(() -> new CardNotFoundException("Card with ID " + cardId + " not found."));

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

        BankAccountDto bankAccount = accountClient.getAccountById(card.getAccountId()).getData();

        CustomerDto customer = customerClient.getCustomerById(bankAccount.getCustomerId())
                .getData();
        try {
            notificationClient.createNotification(CreateNotificationDto.builder()
                    .customerId(customer.getId())
                    .customerEmail(customer.getEmail())
                    .title("Card Deactivated")
                    .type("CARD")
                    .message("Dear " + customer.getFirstName() + ", your card with number "
                            + card.getCardNumber() + " has been deactivated.")
                    .build());
        } catch (Exception e) {
            log.error("Failed to send card deactivation notification for customer id:" + customer.getId(), e);
        }
    }

    @Override
    public void blockCard(Long cardId) {
        Card card = cardRepo.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card with ID " + cardId + " not found."));

        card.setStatus(CardStatus.BLOCKED);

        cardRepo.save(card);

        BankAccountDto bankAccount = accountClient.getAccountById(card.getAccountId()).getData();

        CustomerDto customer = customerClient.getCustomerById(bankAccount.getCustomerId())
                .getData();
        try {

            notificationClient.createNotification(CreateNotificationDto.builder()
                    .customerId(customer.getId())
                    .customerEmail(customer.getEmail())
                    .title("Card Blocked")
                    .type("CARD")
                    .message("Dear " + customer.getFirstName() + ", your card with number "
                            + card.getCardNumber() + " has been blocked.")
                    .build());
        } catch (Exception e) {
            log.error("Failed to send card block notification for customer id: " + customer.getId(), e);
        }

    }

    @Override
    public void deleteCard(Long cardId) {
        Card card = cardRepo.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card with ID " + cardId + " not found."));

        cardRepo.delete(card);

        BankAccountDto bankAccount = accountClient.getAccountById(card.getAccountId()).getData();

        CustomerDto customer = customerClient.getCustomerById(bankAccount.getCustomerId())
                .getData();
        try {

            notificationClient.createNotification(CreateNotificationDto.builder()
                    .customerId(customer.getId())
                    .customerEmail(customer.getEmail())
                    .title("Card Deleted")
                    .type("CARD")
                    .message("Dear " + customer.getFirstName() + ", your card with number "
                            + card.getCardNumber() + " has been deleted successfully.")
                    .build());
        } catch (Exception e) {
            log.error("Failed to send card deletion notification for customer id: " + customer.getId(), e);
        }
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
        try {
            BankAccountDto bankAccount = accountClient.getAccountById(card.getAccountId()).getData();
            CustomerDto customer = customerClient.getCustomerById(bankAccount.getCustomerId())
                    .getData();

            notificationClient.createNotification(CreateNotificationDto.builder()
                    .customerId(customer.getId())
                    .customerEmail(customer.getEmail())
                    .title("Card Status Updated")
                    .type("CARD")
                    .message("Dear " + customer.getFirstName() + ", your card status has been updated to " + newStatus
                            + ".")
                    .build());
        } catch (Exception e) {
            log.error("Failed to send card status update notification for customer id {}", card.getAccountId(), e);
        }
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

        BankAccountDto bankAccount = accountClient.getAccountById(oldCard.getAccountId()).getData();

        CustomerDto customer = customerClient.getCustomerById(bankAccount.getCustomerId())
                .getData();

        try {
            notificationClient.createNotification(CreateNotificationDto.builder()
                    .customerId(customer.getId())
                    .customerEmail(customer.getEmail())
                    .title("Card Regenerated")
                    .type("CARD")
                    .message("Dear " + customer.getFirstName() + ", your card with number "
                            + oldCard.getCardNumber() + " has been replaced with a new card "
                            + savedNewCard.getCardNumber() + ". The old card is now inactive.")
                    .build());
        } catch (Exception e) {
            log.error("Failed to send card regeneration notification for customer id {}", customer.getId(), e);
        }
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
            throw new CardNotActiveException("Card with ID " + cardId + " is not active.");
        }

        if (card.getExpiryDate() != null && card.getExpiryDate().isBefore(LocalDate.now())) {
            throw new CardExpiredException("Card with ID " + cardId + " is expired.");
        }

        ApiResponse<BankAccountDto> accountResponse = accountClient.getAccountById(card.getAccountId());
        if (accountResponse == null || accountResponse.getData() == null) {
            throw new BankAccountNotFoundException("Bank account not found for accountId " + card.getAccountId());
        }

        BankAccountDto bankAccount = accountResponse.getData();

        BigDecimal amount = request.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero.");
        }

        if (bankAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance in account " + bankAccount.getId());
        }

        ApiResponse<TransactionResponse> withdrawResponse = transactionClient.withdraw(request);

        if (withdrawResponse == null || !withdrawResponse.isSuccess()) {
            throw new IllegalStateException("Failed to withdraw amount for cardId " + cardId);
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
            throw new BankAccountNotFoundException("Bank account not found for accountId " + card.getAccountId());
        }

        BigDecimal amount = request.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero.");
        }

        ApiResponse<TransactionResponse> depositResponse = transactionClient.deposit(request);

        if (depositResponse == null || !depositResponse.isSuccess()) {
            throw new IllegalStateException("Failed to deposit amount.");
        }

        return depositResponse.getData();
    }
}