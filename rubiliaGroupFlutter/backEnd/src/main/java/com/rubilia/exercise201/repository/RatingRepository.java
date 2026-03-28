package com.rubilia.exercise201.repository;

import com.rubilia.exercise201.entity.Rating;
import com.rubilia.exercise201.entity.RatingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

public interface RatingRepository extends JpaRepository<Rating, UUID> {
    @Query("SELECT r FROM Rating r WHERE r.product.id = :productId AND r.status = :status")
    List<Rating> findByProductIdAndStatus(@Param("productId") UUID productId, @Param("status") RatingStatus status);

    List<Rating> findByStatus(RatingStatus status);
}