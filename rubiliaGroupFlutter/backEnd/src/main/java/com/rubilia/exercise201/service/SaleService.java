package com.rubilia.exercise201.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.rubilia.exercise201.entity.Sale;

public interface SaleService {
    List<Sale> findAll();
    Optional<Sale> findById(UUID id);
    Sale save(Sale sale, UUID staffId);
    Sale update(UUID id, Sale sale, UUID staffId);
    void deleteById(UUID id);
    boolean existsById(UUID id);
    boolean existsByProductId(UUID productId);
    Sale assignProductToSale(UUID saleId, UUID productId);
    void removeProductFromSale(UUID saleId);
}