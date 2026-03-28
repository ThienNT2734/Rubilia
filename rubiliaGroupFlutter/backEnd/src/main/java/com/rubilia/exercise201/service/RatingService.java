package com.rubilia.exercise201.service;

import com.rubilia.exercise201.entity.Rating;
import com.rubilia.exercise201.entity.RatingStatus;
import java.util.List;
import java.util.UUID;

public interface RatingService {
    Rating createRating(String email, Rating rating);
    List<Rating> getRatingsByProductId(UUID productId, RatingStatus status);
    List<Rating> getRatingsByStatus(RatingStatus status);
    Rating approveRating(UUID ratingId);
    void deleteRating(UUID ratingId);
    List<Rating> getAllRatings(); // Thêm phương thức mới
}