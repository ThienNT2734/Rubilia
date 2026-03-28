package com.rubilia.exercise201.controller;

import com.rubilia.exercise201.entity.Rating;
import com.rubilia.exercise201.entity.RatingStatus;
import com.rubilia.exercise201.entity.Product;
import com.rubilia.exercise201.repository.ProductRepository;
import com.rubilia.exercise201.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;
    private final ProductRepository productRepository;
    private static final Logger logger = LoggerFactory.getLogger(RatingController.class);

    @PostMapping
    public ResponseEntity<?> createRating(@RequestParam String email, @RequestBody Rating rating) {
        try {
            logger.info("Nhận yêu cầu tạo đánh giá với email: {} và dữ liệu: {}", email, rating);
            if (rating.getProductId() == null) {
                logger.warn("Product ID không được cung cấp trong yêu cầu");
                return ResponseEntity.badRequest().body("Product ID is required");
            }
            UUID productId = rating.getProductId();
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));
            rating.setProduct(product);
            Rating createdRating = ratingService.createRating(email, rating);
            logger.info("Tạo đánh giá thành công với ID: {}", createdRating.getId());
            return ResponseEntity.ok(createdRating);
        } catch (IllegalArgumentException e) {
            logger.error("Lỗi khi tạo đánh giá: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Lỗi không xác định khi tạo đánh giá: {}", e.getMessage());
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Rating>> getApprovedRatingsByProductId(@PathVariable UUID productId) {
        List<Rating> ratings = ratingService.getRatingsByProductId(productId, RatingStatus.APPROVED);
        return ResponseEntity.ok(ratings);
    }

    @GetMapping
    public ResponseEntity<List<Rating>> getRatingsByStatus(
            @RequestParam(name = "status", defaultValue = "PENDING") String status) {
        RatingStatus ratingStatus = RatingStatus.valueOf(status.toUpperCase());
        List<Rating> ratings = ratingService.getRatingsByStatus(ratingStatus);
        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Rating>> getAllRatings() {
        List<Rating> ratings = ratingService.getAllRatings();
        return ResponseEntity.ok(ratings);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<Rating> approveRating(@PathVariable UUID id) {
        Rating approvedRating = ratingService.approveRating(id);
        return ResponseEntity.ok(approvedRating);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRating(@PathVariable UUID id) {
        ratingService.deleteRating(id);
        return ResponseEntity.noContent().build();
    }
}