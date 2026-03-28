package com.rubilia.exercise201.service;

import com.rubilia.exercise201.entity.ReviewPost;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewPostService {
    List<ReviewPost> findAll();
    Optional<ReviewPost> findById(UUID id);
    List<ReviewPost> findByProductId(UUID productId);
    ReviewPost save(ReviewPost reviewPost, UUID staffId);
    ReviewPost update(UUID id, ReviewPost reviewPost, UUID staffId);
    void deleteById(UUID id);
    boolean existsById(UUID id);
}