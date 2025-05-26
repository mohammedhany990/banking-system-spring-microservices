package com.bankingsystem.card.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import com.bankingsystem.card.entity.CardNetwork;
import com.bankingsystem.card.entity.CardType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CardRequestDto {

    // @NotBlank(message = "Card holder name is required")
    //private String cardHolderName;

    @NotNull(message = "Card network is required")
    private CardNetwork cardNetwork;

    @NotNull(message = "Card type is required")
    private CardType cardType;

    @NotNull(message = "Account ID is required")
    private Long accountId;

}
