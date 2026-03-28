package com.rubilia.exercise201.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.rubilia.exercise201.entity.Product;
import com.rubilia.exercise201.entity.ProductTag;
import com.rubilia.exercise201.entity.ProductTagId;
import com.rubilia.exercise201.entity.Tag;

@Repository
public interface ProductTagRepository extends JpaRepository<ProductTag, ProductTagId> {
    List<ProductTag> findByProduct(Product product);

    List<ProductTag> findByTag(Tag tag);

    void deleteByProductAndTag(Product product, Tag tag);

    @Modifying
    @Transactional
    @Query("DELETE FROM ProductTag pt WHERE pt.product.id = :productId")
    void deleteByProductId(UUID productId);
}