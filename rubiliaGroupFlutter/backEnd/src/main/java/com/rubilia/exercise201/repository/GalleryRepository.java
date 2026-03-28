package com.rubilia.exercise201.repository;

import java.util.UUID;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.rubilia.exercise201.entity.Gallery;
import com.rubilia.exercise201.entity.Product;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@Repository
@RepositoryRestResource(path = "gallerys")
public interface GalleryRepository extends JpaRepository<Gallery, UUID> {
    List<Gallery> findByProduct(Product product);

    // Tìm toàn bộ ảnh theo sản phẩm
    @Query("SELECT g FROM Gallery g WHERE g.product.id = :productId")
    List<Gallery> findByProductId(UUID productId);

    // Tìm ảnh theo sản phẩm và là ảnh chính
    @Query("SELECT g FROM Gallery g WHERE g.product.id = :productId AND g.isThumbnail = true")
    List<Gallery> findThumbnailByProductId(UUID productId);

    // Tìm gallery theo sản phẩm và là ảnh thumbnail
    List<Gallery> findByProductAndIsThumbnailTrue(Product product);

    @Modifying
    @Transactional
    @Query("DELETE FROM Gallery g WHERE g.product.id = :productId")
    void deleteAllByProductId(UUID productId);
}