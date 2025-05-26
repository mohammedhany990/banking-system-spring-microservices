package com.bankingsystem.card.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bankingsystem.card.entity.Card;

@Repository
public interface CardRepo extends JpaRepository<Card, Long> {
    List<Card> findByAccountId(Long accountId);

}
