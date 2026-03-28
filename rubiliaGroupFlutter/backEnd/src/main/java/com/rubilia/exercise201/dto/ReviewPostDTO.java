package com.rubilia.exercise201.dto;

import java.util.UUID;

public class ReviewPostDTO {
    private String title;
    private String content;
    private String imageUrl; // Thêm trường imageUrl
    private UUID productId;

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    @Override
    public String toString() {
        return "ReviewPostDTO{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", productId=" + productId +
                '}';
    }
}