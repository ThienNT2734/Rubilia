package com.rubilia.exercise201.repository;

import com.rubilia.exercise201.entity.Product;
import com.rubilia.exercise201.entity.ReviewPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewPostRepository extends JpaRepository<ReviewPost, UUID> {
    List<ReviewPost> findByProduct(Product product);
}