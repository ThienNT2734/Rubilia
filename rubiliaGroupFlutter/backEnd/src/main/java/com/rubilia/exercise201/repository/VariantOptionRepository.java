package com.rubilia.exercise201.repository;

import java.util.UUID;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.rubilia.exercise201.entity.Product;
import com.rubilia.exercise201.entity.VariantOption;

@Repository
public interface VariantOptionRepository extends JpaRepository<VariantOption, UUID> {
    List<VariantOption> findByProduct(Product product);

    List<VariantOption> findByProductAndActiveTrue(Product product);

    @Modifying
    @Query("DELETE FROM VariantOption vo WHERE vo.image.id = :imageId")
    void deleteByImageId(UUID imageId);
}