package com.rubilia.exercise201.repository;

import com.rubilia.exercise201.entity.Country;
import com.rubilia.exercise201.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, UUID> {
    boolean existsBySupplierName(String supplierName);
    List<Supplier> findByCountry(Country country);
    List<Supplier> findByCompanyContaining(String company);
}