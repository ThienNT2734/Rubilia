package com.rubilia.exercise201.service.impl;

import com.rubilia.exercise201.entity.Comment;
import com.rubilia.exercise201.entity.CommentStatus;
import com.rubilia.exercise201.repository.CommentRepository;
import com.rubilia.exercise201.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    @Override
    public Comment createComment(String email, Comment comment) {
        comment.setEmail(email);
        comment.setStatus(CommentStatus.PENDING);
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
        return commentRepository.findAll(); // Lấy tất cả bình luận
    }
}