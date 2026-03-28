package com.rubilia.exercise201.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonNode;
import com.rubilia.exercise201.entity.Card;
import com.rubilia.exercise201.entity.CardItem;
import com.rubilia.exercise201.entity.Customer;

public interface CardService {
    List<Card> getAllCards();

    public ResponseEntity<?> save(JsonNode jsonNode);

    public ResponseEntity<?> updateQuantity(UUID customerId, UUID productId, int newQuantity);

    public ResponseEntity<?> removeProductFromCart(UUID customerId, UUID productId);
}