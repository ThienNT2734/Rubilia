package com.rubilia.exercise201.controller;

import com.rubilia.exercise201.entity.Comment;
import com.rubilia.exercise201.entity.CommentStatus;
import com.rubilia.exercise201.entity.Product;
import com.rubilia.exercise201.repository.ProductRepository;
import com.rubilia.exercise201.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final ProductRepository productRepository;
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @PostMapping
    public ResponseEntity<?> createComment(@RequestParam String email, @RequestBody Comment comment) {
        try {
            logger.info("Nhận yêu cầu tạo bình luận với email: {} và dữ liệu: {}", email, comment);
            if (comment.getProductId() == null) {
                logger.warn("Product ID không được cung cấp trong yêu cầu");
                return ResponseEntity.badRequest().body("Product ID is required");
            }
            UUID productId = comment.getProductId();
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));
            comment.setProduct(product);
            Comment createdComment = commentService.createComment(email, comment);
            logger.info("Tạo bình luận thành công với ID: {}", createdComment.getId());
            return ResponseEntity.ok(createdComment);
        } catch (IllegalArgumentException e) {
            logger.error("Lỗi khi tạo bình luận: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Lỗi không xác định khi tạo bình luận: {}", e.getMessage());
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Comment>> getApprovedCommentsByProductId(@PathVariable UUID productId) {
        List<Comment> comments = commentService.getCommentsByProductId(productId, CommentStatus.APPROVED);
        return ResponseEntity.ok(comments);
    }

    @GetMapping
    public ResponseEntity<List<Comment>> getCommentsByStatus(
            @RequestParam(name = "status", defaultValue = "PENDING") String status) {
        CommentStatus commentStatus = CommentStatus.valueOf(status.toUpperCase());
        List<Comment> comments = commentService.getCommentsByStatus(commentStatus);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Comment>> getAllComments() {
        List<Comment> comments = commentService.getAllComments();
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<Comment> approveComment(@PathVariable UUID id) {
        Comment approvedComment = commentService.approveComment(id);
        return ResponseEntity.ok(approvedComment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}