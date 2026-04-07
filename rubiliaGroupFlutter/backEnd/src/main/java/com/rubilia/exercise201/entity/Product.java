package com.rubilia.exercise201.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;
import java.util.UUID;
import java.util.List;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "compare_price")
    private BigDecimal comparePrice = BigDecimal.ZERO;

    @Column
    private String sku;

    @Column(name = "price", nullable = false)
    private BigDecimal price = BigDecimal.ZERO;

    @JsonProperty("discountPercentage")
    @Column(name = "discount_percentage")
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    @JsonProperty("isOnPromotion")
    @Column(name = "is_on_promotion")
    private Boolean isOnPromotion = false;
    
    @JsonProperty("promotionStart")
    @Column(name = "promotion_start")
    @Temporal(TemporalType.TIMESTAMP)
    private Date promotionStart;
    
    @JsonProperty("promotionEnd")
    @Column(name = "promotion_end")
    @Temporal(TemporalType.TIMESTAMP)
    private Date promotionEnd;

    public BigDecimal getSalePrice() {
        return price;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.price = salePrice;
    }

    public BigDecimal getComparePrice() {
        return comparePrice;
    }

    public void setComparePrice(BigDecimal comparePrice) {
        this.comparePrice = comparePrice;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    // Promotion getters and setters
    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public Date getPromotionStart() {
        return promotionStart;
    }

    public void setPromotionStart(Date promotionStart) {
        this.promotionStart = promotionStart;
    }

    public Date getPromotionEnd() {
        return promotionEnd;
    }

    public void setPromotionEnd(Date promotionEnd) {
        this.promotionEnd = promotionEnd;
    }

    public Boolean getIsOnPromotion() {
        return isOnPromotion;
    }

    public void setIsOnPromotion(Boolean isOnPromotion) {
        this.isOnPromotion = isOnPromotion;
    }

    // Get the effective price considering promotion
    public BigDecimal getEffectivePrice() {
        if (isOnPromotion != null && isOnPromotion && discountPercentage != null && discountPercentage.compareTo(BigDecimal.ZERO) > 0) {
            Date now = new Date();
            if (promotionStart != null && promotionEnd != null &&
                now.after(promotionStart) && now.before(promotionEnd)) {
                return price.subtract(price.multiply(discountPercentage.divide(BigDecimal.valueOf(100))));
            }
        }
        return price;
    }

    @Column(name = "buying_price")
    private BigDecimal buyingPrice;

    @Column(nullable = false)
    private Integer quantity = 0;

    @Column(name = "short_description", nullable = false, length = 165)
    private String shortDescription;

    @Column(name = "product_description", nullable = false, columnDefinition = "TEXT")
    private String productDescription;

    @Column(length = 64)
    @Enumerated(EnumType.STRING)
    private ProductType productType;

    @Column
    private Boolean published = false;

    @Column(name = "disable_out_of_stock")
    private Boolean disableOutOfStock = true;

    @Column
    private String note;

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    @JsonIgnore
    private StaffAccount createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    @JsonIgnore
    private StaffAccount updatedBy;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ProductCategory> productCategories;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, optional = true)
    @JsonIgnore
    private ProductShippingInfo shippingInfo;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Gallery> galleries;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ProductDisplayInfo> displayInfos;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ProductSupplier> productSuppliers;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ProductTag> productTags;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<VariantOption> variantOptions;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<OrderItem> orderItems;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<CardItem> cardItems;

    public enum ProductType {
        simple,
        variable
    }

    
}