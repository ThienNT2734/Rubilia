package com.rubilia.exercise201.service.impl;

import com.rubilia.exercise201.entity.Product;
import com.rubilia.exercise201.entity.ReviewPost;
import com.rubilia.exercise201.entity.StaffAccount;
import com.rubilia.exercise201.repository.ProductRepository;
import com.rubilia.exercise201.repository.ReviewPostRepository;
import com.rubilia.exercise201.repository.StaffAccountRepository;
import com.rubilia.exercise201.service.ReviewPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ReviewPostServiceImpl implements ReviewPostService {

    @Autowired
    private ReviewPostRepository reviewPostRepository;

    @Autowired
    private StaffAccountRepository staffAccountRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<ReviewPost> findAll() {
        return reviewPostRepository.findAll();
    }

    @Override
    public Optional<ReviewPost> findById(UUID id) {
        return reviewPostRepository.findById(id);
    }

    @Override
    public List<ReviewPost> findByProductId(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));
        return reviewPostRepository.findByProduct(product);
    }

    @Override
    public ReviewPost save(ReviewPost reviewPost, UUID staffId) {
        StaffAccount staff = staffAccountRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found with ID: " + staffId));

        // Kiểm tra và ánh xạ product
        if (reviewPost.getProduct() == null || reviewPost.getProduct().getId() == null) {
            throw new IllegalArgumentException("Product ID is required");
        }
        Product product = productRepository.findById(reviewPost.getProduct().getId())
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + reviewPost.getProduct().getId()));
        reviewPost.setProduct(product);

        // Thiết lập các trường bắt buộc
        reviewPost.setCreatedAt(new Date());
        reviewPost.setUpdatedAt(new Date());
        reviewPost.setCreatedBy(staff);
        reviewPost.setUpdatedBy(staff);

        return reviewPostRepository.save(reviewPost);
    }

    @Override
    public ReviewPost update(UUID id, ReviewPost reviewPost, UUID staffId) {
        ReviewPost existingReviewPost = reviewPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review post not found with ID: " + id));
        StaffAccount staff = staffAccountRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found with ID: " + staffId));

        existingReviewPost.setTitle(reviewPost.getTitle());
        existingReviewPost.setContent(reviewPost.getContent());
        existingReviewPost.setImageUrl(reviewPost.getImageUrl()); // Cập nhật imageUrl

        // Kiểm tra và ánh xạ product
        if (reviewPost.getProduct() == null || reviewPost.getProduct().getId() == null) {
            throw new IllegalArgumentException("Product ID is required");
        }
        Product product = productRepository.findById(reviewPost.getProduct().getId())
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + reviewPost.getProduct().getId()));
        existingReviewPost.setProduct(product);

        existingReviewPost.setUpdatedAt(new Date());
        existingReviewPost.setUpdatedBy(staff);

        return reviewPostRepository.save(existingReviewPost);
    }

    @Override
    public void deleteById(UUID id) {
        reviewPostRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return reviewPostRepository.existsById(id);
    }
}