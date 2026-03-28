package com.rubilia.exercise201.service;

import com.rubilia.exercise201.entity.Comment;
import com.rubilia.exercise201.entity.CommentStatus;
import java.util.List;
import java.util.UUID;

public interface CommentService {
    Comment createComment(String email, Comment comment);
    List<Comment> getCommentsByProductId(UUID productId, CommentStatus status);
    List<Comment> getCommentsByStatus(CommentStatus status);
    Comment approveComment(UUID commentId);
    void deleteComment(UUID commentId);
    List<Comment> getAllComments(); // Thêm phương thức mới
}