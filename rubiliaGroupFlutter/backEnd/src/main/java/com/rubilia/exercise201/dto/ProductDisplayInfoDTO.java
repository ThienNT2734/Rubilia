package com.rubilia.exercise201.dto;

import java.math.BigDecimal;

public class ProductDisplayInfoDTO {
    private Long id;
    private String displayArea;
    private BigDecimal salesCount;
    private BigDecimal rating;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDisplayArea() {
        return displayArea;
    }

    public void setDisplayArea(String displayArea) {
        this.displayArea = displayArea;
    }

    public BigDecimal getSalesCount() {
        return salesCount;
    }

    public void setSalesCount(BigDecimal salesCount) {
        this.salesCount = salesCount;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }
}