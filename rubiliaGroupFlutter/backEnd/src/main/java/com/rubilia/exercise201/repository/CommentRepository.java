package com.rubilia.exercise201.repository;

import com.rubilia.exercise201.entity.Comment;
import com.rubilia.exercise201.entity.CommentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    @Query("SELECT c FROM Comment c WHERE c.product.id = :productId AND c.status = :status")
    List<Comment> findByProductIdAndStatus(@Param("productId") UUID productId, @Param("status") CommentStatus status);

    List<Comment> findByStatus(CommentStatus status);
}