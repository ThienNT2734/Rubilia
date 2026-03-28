package com.rubilia.exercise201.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonNode;
import com.rubilia.exercise201.entity.Product;

public interface ProductService {
    List<Product> getAllProducts();

    Optional<Product> getProductById(UUID id);

    ResponseEntity<?> save(JsonNode productJson, UUID staffId);

    ResponseEntity<?> update(UUID productId, JsonNode productJson, UUID staffId);

    ResponseEntity<?> deleteProduct(UUID productId);

    List<Product> searchProducts(String keyword);

    List<Product> getProductsByDisplayArea(String displayArea);
}