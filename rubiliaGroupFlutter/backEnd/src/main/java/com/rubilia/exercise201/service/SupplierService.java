package com.rubilia.exercise201.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.rubilia.exercise201.entity.Country;
import com.rubilia.exercise201.entity.Supplier;

public interface SupplierService {
    List<Supplier> findAll();
    Optional<Supplier> findById(UUID id);
    Supplier save(Supplier supplier);
    void deleteById(UUID id);
    boolean existsById(UUID id);
    boolean existsBySupplierName(String supplierName);
    List<Supplier> findByCountry(Country country);
    List<Supplier> findByCompanyContaining(String company);
}