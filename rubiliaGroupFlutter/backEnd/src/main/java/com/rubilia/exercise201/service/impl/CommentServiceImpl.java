package com.rubilia.exercise201.service.impl;

import com.rubilia.exercise201.dto.SentimentAnalysisResult;
import com.rubilia.exercise201.entity.Comment;
import com.rubilia.exercise201.entity.CommentStatus;
import com.rubilia.exercise201.entity.SentimentType;
import com.rubilia.exercise201.repository.CommentRepository;
import com.rubilia.exercise201.service.CommentService;
import com.rubilia.exercise201.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired; // THÊM DÒNG NÀY
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
// XÓA @RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    @Autowired // Thay vì dùng private final
    private CommentRepository commentRepository;

    @Autowired // Thay vì dùng private final
    private GeminiService geminiService;

    @Override
    public Comment createComment(String email, Comment comment) {
        comment.setEmail(email);
        comment.setStatus(CommentStatus.PENDING);

        try {
            // Gọi AI phân tích cảm xúc
            SentimentAnalysisResult result = geminiService.analyzeComment(
                    comment.getProduct() != null ? comment.getProduct().getProductName() : null,
                    comment.getContent()
            );

            // Kiểm tra result khác null trước khi lấy dữ liệu
            if (result != null) {
                comment.setSentiment(result.getSentiment() != null ? result.getSentiment() : SentimentType.UNKNOWN);
                comment.setSentimentScore(result.getScore());
                comment.setSentimentExplanation(result.getExplanation());
                comment.setAnalyzedAt(LocalDateTime.now());
            }
        } catch (Exception e) {
            // Nếu AI lỗi, set mặc định để không làm treo ứng dụng
            comment.setSentiment(SentimentType.UNKNOWN);
        }

        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getCommentsByProductId(UUID productId, CommentStatus status) {
        return commentRepository.findByProductIdAndStatus(productId, status);
    }

    @Override
    public List<Comment> getCommentsByStatus(CommentStatus status) {
        return commentRepository.findByStatus(status);
    }

    @Override
    public Comment approveComment(UUID commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + commentId));
        comment.setStatus(CommentStatus.APPROVED);
        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(UUID commentId) {
        commentRepository.deleteById(commentId);
    }

    @Override
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }
}