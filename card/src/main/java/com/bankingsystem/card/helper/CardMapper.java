package com.bankingsystem.card.helper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.bankingsystem.card.dto.CardResponse;
import com.bankingsystem.card.entity.Card;

import org.mapstruct.factory.Mappers;
@Mapper(componentModel = "spring")
public interface CardMapper {

    CardMapper INSTANCE = Mappers.getMapper(CardMapper.class);

    CardResponse toCardResponse(Card card);

    @Mapping(target = "cvv", ignore = true)
    Card toCard(CardResponse cardResponse);

    List<CardResponse> toCardResponseList(List<Card> cards);
}

