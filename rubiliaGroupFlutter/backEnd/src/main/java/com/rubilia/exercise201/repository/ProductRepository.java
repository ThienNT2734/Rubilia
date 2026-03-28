package com.rubilia.exercise201.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.rubilia.exercise201.entity.Product;

@RepositoryRestResource(path = "products")
public interface ProductRepository extends JpaRepository<Product, UUID> {

    List<Product> findByProductNameContainingIgnoreCase(String keyword);

    @Query("SELECT p FROM Product p JOIN p.displayInfos di WHERE di.displayArea = :displayArea")
    List<Product> findByDisplayArea(@Param("displayArea") String displayArea);

    @Modifying
    @Query("DELETE FROM ProductDisplayInfo di WHERE di.product.id = :productId")
    void deleteDisplayInfosByProductId(@Param("productId") UUID productId);

    // Phương thức mới để kiểm tra trùng lặp slug
    Optional<Product> findBySlug(String slug);
}