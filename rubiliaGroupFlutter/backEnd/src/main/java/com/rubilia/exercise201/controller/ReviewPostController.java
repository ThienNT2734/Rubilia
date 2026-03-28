package com.rubilia.exercise201.controller;

import com.rubilia.exercise201.dto.ReviewPostDTO;
import com.rubilia.exercise201.entity.Product;
import com.rubilia.exercise201.entity.ReviewPost;
import com.rubilia.exercise201.service.ReviewPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/review-posts")
public class ReviewPostController {

    @Autowired
    private ReviewPostService reviewPostService;

    @GetMapping(produces = "application/json")
    public ResponseEntity<List<ReviewPost>> getAllReviewPosts() {
        try {
            System.out.println("Nhận yêu cầu GET /api/review-posts");
            List<ReviewPost> reviewPosts = reviewPostService.findAll();
            System.out.println("Trả về danh sách bài viết: " + reviewPosts);
            return ResponseEntity.ok(reviewPosts);
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy danh sách bài viết: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<ReviewPost> getReviewPostById(@PathVariable UUID id) {
        try {
            System.out.println("Nhận yêu cầu GET /api/review-posts/" + id);
            Optional<ReviewPost> reviewPost = reviewPostService.findById(id);
            if (reviewPost.isPresent()) {
                System.out.println("Trả về bài viết: " + reviewPost.get());
                return ResponseEntity.ok(reviewPost.get());
            } else {
                System.out.println("Không tìm thấy bài viết với ID: " + id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy bài viết với ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/product/{productId}", produces = "application/json")
    public ResponseEntity<List<ReviewPost>> getReviewPostsByProduct(@PathVariable UUID productId) {
        try {
            System.out.println("Nhận yêu cầu GET /api/review-posts/product/" + productId);
            List<ReviewPost> reviewPosts = reviewPostService.findByProductId(productId);
            System.out.println("Trả về danh sách bài viết cho sản phẩm: " + reviewPosts);
            return ResponseEntity.ok(reviewPosts);
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy danh sách bài viết cho sản phẩm " + productId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(produces = "application/json")
    public ResponseEntity<?> createReviewPost(@RequestBody ReviewPostDTO reviewPostDTO, @RequestParam UUID staffId) {
        try {
            System.out.println("Nhận yêu cầu POST /api/review-posts với dữ liệu: " + reviewPostDTO);
            ReviewPost reviewPost = new ReviewPost();
            reviewPost.setTitle(reviewPostDTO.getTitle());
            reviewPost.setContent(reviewPostDTO.getContent());
            reviewPost.setImageUrl(reviewPostDTO.getImageUrl()); // Thêm imageUrl
            Product product = new Product();
            product.setId(reviewPostDTO.getProductId());
            reviewPost.setProduct(product);
            ReviewPost savedReviewPost = reviewPostService.save(reviewPost, staffId);
            System.out.println("Bài viết đã lưu: " + savedReviewPost);
            return ResponseEntity.ok(savedReviewPost);
        } catch (Exception e) {
            System.err.println("Lỗi khi lưu bài viết: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<?> updateReviewPost(
            @PathVariable UUID id,
            @RequestBody ReviewPostDTO reviewPostDTO,
            @RequestParam UUID staffId) {
        try {
            System.out.println("Nhận yêu cầu PUT /api/review-posts/" + id + " với dữ liệu: " + reviewPostDTO);
            if (!reviewPostService.existsById(id)) {
                System.out.println("Không tìm thấy bài viết với ID: " + id);
                return ResponseEntity.notFound().build();
            }
            ReviewPost reviewPost = new ReviewPost();
            reviewPost.setTitle(reviewPostDTO.getTitle());
            reviewPost.setContent(reviewPostDTO.getContent());
            reviewPost.setImageUrl(reviewPostDTO.getImageUrl()); // Thêm imageUrl
            Product product = new Product();
            product.setId(reviewPostDTO.getProductId());
            reviewPost.setProduct(product);
            ReviewPost updatedReviewPost = reviewPostService.update(id, reviewPost, staffId);
            System.out.println("Bài viết đã cập nhật: " + updatedReviewPost);
            return ResponseEntity.ok(updatedReviewPost);
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật bài viết: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReviewPost(@PathVariable UUID id) {
        try {
            System.out.println("Nhận yêu cầu DELETE /api/review-posts/" + id);
            if (!reviewPostService.existsById(id)) {
                System.out.println("Không tìm thấy bài viết với ID: " + id);
                return ResponseEntity.notFound().build();
            }
            reviewPostService.deleteById(id);
            System.out.println("Đã xóa bài viết với ID: " + id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("Lỗi khi xóa bài viết với ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}