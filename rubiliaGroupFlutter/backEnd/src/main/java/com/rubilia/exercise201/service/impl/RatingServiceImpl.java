package com.rubilia.exercise201.service.impl;

import com.rubilia.exercise201.entity.Rating;
import com.rubilia.exercise201.entity.RatingStatus;
import com.rubilia.exercise201.repository.RatingRepository;
import com.rubilia.exercise201.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;

    @Override
    public Rating createRating(String email, Rating rating) {
        if (rating.getRating() < 1 || rating.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        rating.setEmail(email);
        rating.setStatus(RatingStatus.PENDING);
        return ratingRepository.save(rating);
    }

    @Override
    public List<Rating> getRatingsByProductId(UUID productId, RatingStatus status) {
        return ratingRepository.findByProductIdAndStatus(productId, status);
    }

    @Override
    public List<Rating> getRatingsByStatus(RatingStatus status) {
        return ratingRepository.findByStatus(status);
    }

    @Override
    public Rating approveRating(UUID ratingId) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new IllegalArgumentException("Rating not found: " + ratingId));
        rating.setStatus(RatingStatus.APPROVED);
        return ratingRepository.save(rating);
    }

    @Override
    public void deleteRating(UUID ratingId) {
        ratingRepository.deleteById(ratingId);
    }

    @Override
    public List<Rating> getAllRatings() {
        return ratingRepository.findAll(); // Lấy tất cả đánh giá
    }
}