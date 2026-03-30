package com.rubilia.exercise201.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommentStatus status = CommentStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "sentiment")
    private SentimentType sentiment = SentimentType.UNKNOWN;
    
    @Column(name = "sentiment_score")
    private Double sentimentScore;
    
    @Column(name = "sentiment_explanation", columnDefinition = "TEXT")
    private String sentimentExplanation;
    
    @Column(name = "analyzed_at")
    private LocalDateTime analyzedAt;

    @Column(nullable = false)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    private Product product;

    @Transient
    private UUID productId;

    // Method lấy productId để trả về JSON
    @JsonProperty("productId")
    public UUID getProductId() {
        return product != null ? product.getId() : productId;
    }

    // Method lấy productName để trả về JSON
    @JsonProperty("productName")
    public String getProductName() {
        return product != null ? product.getProductName() : null;
    }

    // Tự động fill productId khi load từ DB
    @PostLoad
    protected void postLoad() {
        if (product != null) {
            this.productId = product.getId();
        }
    }

    // Tự động set createdAt khi tạo mới
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}