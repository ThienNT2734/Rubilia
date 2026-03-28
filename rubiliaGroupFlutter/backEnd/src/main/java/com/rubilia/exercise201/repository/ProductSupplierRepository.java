package com.rubilia.exercise201.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.rubilia.exercise201.entity.Product;
import com.rubilia.exercise201.entity.ProductSupplier;
import com.rubilia.exercise201.entity.ProductSupplierId;
import com.rubilia.exercise201.entity.Supplier;

@Repository
public interface ProductSupplierRepository extends JpaRepository<ProductSupplier, ProductSupplierId> {
    List<ProductSupplier> findByProduct(Product product);

    List<ProductSupplier> findBySupplier(Supplier supplier);

    void deleteByProductAndSupplier(Product product, Supplier supplier);

    @Modifying
    @Transactional
    @Query("DELETE FROM ProductSupplier ps WHERE ps.product.id = :productId")
    void deleteByProductId(UUID productId);
}