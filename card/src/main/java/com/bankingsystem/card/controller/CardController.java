package com.bankingsystem.card.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bankingsystem.card.dto.CardRequestDto;
import com.bankingsystem.card.dto.CardResponse;
import com.bankingsystem.card.dto.TransactionResponse;
import com.bankingsystem.card.dto.transactions.DepositRequest;
import com.bankingsystem.card.dto.transactions.TransferRequest;
import com.bankingsystem.card.dto.transactions.WithdrawRequest;
import com.bankingsystem.card.helper.ApiResponse;
import com.bankingsystem.card.service.CardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PostMapping
    public ResponseEntity<ApiResponse<CardResponse>> createCard(@RequestBody CardRequestDto request) {
        CardResponse cardResponse = cardService.createCard(request);
        ApiResponse<CardResponse> response = ApiResponse.<CardResponse>builder()
                .success(true)
                .message("Card created successfully.")
                .data(cardResponse)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CardResponse>> getCardById(@PathVariable Long id) {
        CardResponse cardResponse = cardService.getCardById(id);
        ApiResponse<CardResponse> response = ApiResponse.<CardResponse>builder()
                .success(true)
                .message("Card retrieved successfully.")
                .data(cardResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<ApiResponse<List<CardResponse>>> getCardsByAccountId(@PathVariable Long accountId) {
        List<CardResponse> cards = cardService.getCardsByAccountId(accountId);
        ApiResponse<List<CardResponse>> response = ApiResponse.<List<CardResponse>>builder()
                .success(true)
                .message("Cards retrieved successfully.")
                .data(cards)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivateCard(@PathVariable Long id) {
        cardService.deactivateCard(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Card deactivated successfully.")
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/block")
    public ResponseEntity<ApiResponse<Void>> blockCard(@PathVariable Long id) {
        cardService.blockCard(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Card blocked successfully.")
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Card deleted successfully.")
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<CardResponse>> updateCardStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        CardResponse updatedCard = cardService.updateCardStatus(id, status);
        ApiResponse<CardResponse> response = ApiResponse.<CardResponse>builder()
                .success(true)
                .message("Card status updated successfully.")
                .data(updatedCard)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{oldCardId}/regenerate")
    public ResponseEntity<ApiResponse<CardResponse>> regenerateCard(@PathVariable Long oldCardId) {
        CardResponse newCard = cardService.regenerateCard(oldCardId);
        ApiResponse<CardResponse> response = ApiResponse.<CardResponse>builder()
                .success(true)
                .message("Card regenerated successfully.")
                .data(newCard)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/expired")
    public ResponseEntity<ApiResponse<Boolean>> isCardExpired(@PathVariable Long id) {
        boolean expired = cardService.isCardExpired(id);
        ApiResponse<Boolean> response = ApiResponse.<Boolean>builder()
                .success(true)
                .message("Card expiration checked successfully.")
                .data(expired)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{cardId}/transfer")
    public ResponseEntity<ApiResponse<TransactionResponse>> transferUsingCard(
            @PathVariable Long cardId,
            @RequestBody TransferRequest request) {

        TransactionResponse transactionResponse = cardService.transferUsingCard(cardId, request);

        ApiResponse<TransactionResponse> response = ApiResponse.<TransactionResponse>builder()
                .success(true)
                .message("Transfer completed successfully.")
                .data(transactionResponse)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{cardId}/withdraw")
    public ResponseEntity<ApiResponse<TransactionResponse>> withdrawUsingCard(
            @PathVariable Long cardId,
            @RequestBody WithdrawRequest request) {

        TransactionResponse transactionResponse = cardService.withdrawUsingCard(cardId, request);

        ApiResponse<TransactionResponse> response = ApiResponse.<TransactionResponse>builder()
                .success(true)
                .message("Withdrawal completed successfully.")
                .data(transactionResponse)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{cardId}/deposit")
    public ResponseEntity<ApiResponse<TransactionResponse>> depositUsingCard(
            @PathVariable Long cardId,
            @RequestBody DepositRequest request) {

        TransactionResponse transactionResponse = cardService.depositUsingCard(cardId, request);

        ApiResponse<TransactionResponse> response = ApiResponse.<TransactionResponse>builder()
                .success(true)
                .message("Deposit completed successfully.")
                .data(transactionResponse)
                .build();

        return ResponseEntity.ok(response);
    }

}
