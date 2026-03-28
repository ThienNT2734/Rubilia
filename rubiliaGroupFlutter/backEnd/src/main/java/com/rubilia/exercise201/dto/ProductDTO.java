package com.rubilia.exercise201.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ProductDTO {
    private UUID id;
    private String slug;
    private String productName;
    private String sku;
    private BigDecimal salePrice;
    private BigDecimal comparePrice;
    private BigDecimal buyingPrice;
    private Integer quantity;
    private String shortDescription;
    private String productDescription;
    private String productType;
    private Boolean published;
    private Boolean disableOutOfStock;
    private String note;
    private Date createdAt;
    private Date updatedAt;
    private List<ProductCategoryDTO> productCategories;
    private ProductShippingInfoDTO shippingInfo;
    private List<GalleryDTO> galleries;
    private List<ProductDisplayInfoDTO> displayInfos;

    // Getters and setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public BigDecimal getComparePrice() {
        return comparePrice;
    }

    public void setComparePrice(BigDecimal comparePrice) {
        this.comparePrice = comparePrice;
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

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public Boolean getDisableOutOfStock() {
        return disableOutOfStock;
    }

    public void setDisableOutOfStock(Boolean disableOutOfStock) {
        this.disableOutOfStock = disableOutOfStock;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<ProductCategoryDTO> getProductCategories() {
        return productCategories;
    }

    public void setProductCategories(List<ProductCategoryDTO> productCategories) {
        this.productCategories = productCategories;
    }

    public ProductShippingInfoDTO getShippingInfo() {
        return shippingInfo;
    }

    public void setShippingInfo(ProductShippingInfoDTO shippingInfo) {
        this.shippingInfo = shippingInfo;
    }

    public List<GalleryDTO> getGalleries() {
        return galleries;
    }

    public void setGalleries(List<GalleryDTO> galleries) {
        this.galleries = galleries;
    }

    public List<ProductDisplayInfoDTO> getDisplayInfos() {
        return displayInfos;
    }

    public void setDisplayInfos(List<ProductDisplayInfoDTO> displayInfos) {
        this.displayInfos = displayInfos;
    }
}