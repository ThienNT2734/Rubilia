package com.rubilia.exercise201.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.rubilia.exercise201.entity.ProductShippingInfo;

@Repository
public interface ProductShippingInfoRepository extends JpaRepository<ProductShippingInfo, UUID> {

    @Modifying
    @Transactional
    @Query("DELETE FROM ProductShippingInfo psi WHERE psi.product.id = :productId")
    void deleteByProductId(UUID productId);
}