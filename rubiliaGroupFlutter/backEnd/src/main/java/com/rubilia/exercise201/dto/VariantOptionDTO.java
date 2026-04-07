package com.rubilia.exercise201.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public class VariantOptionDTO {
    private UUID id;
    private String title;
    private UUID imageId; // Only include ID to avoid circular references
    private BigDecimal price;
    private BigDecimal buyingPrice;
    private Integer quantity;
    private String sku;
    private Boolean active;

    // Getters and setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public UUID getImageId() {
        return imageId;
    }

    public void setImageId(UUID imageId) {
        this.imageId = imageId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getSalePrice() {
        return price;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.price = salePrice;
    }

    public BigDecimal getComparePrice() {
        return price;
    }

    public void setComparePrice(BigDecimal comparePrice) {
        this.price = comparePrice;
    }

    public BigDecimal getBuyingPrice() {
        return buyingPrice;
    }

    public void setBuyingPrice(BigDecimal buyingPrice) {
        this.buyingPrice = buyingPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}