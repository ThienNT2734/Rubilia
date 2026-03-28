package com.rubilia.exercise201.entity;

import jakarta.persistence.*;
import java.util.UUID;
import java.math.BigDecimal;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "variant_options")
public class VariantOption {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    @JsonIgnore
    private Gallery image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    private Product product;

    @Column(name = "sale_price", nullable = false)
    private BigDecimal salePrice = BigDecimal.ZERO;

    @Column(name = "compare_price")
    private BigDecimal comparePrice = BigDecimal.ZERO;

    @Column(name = "buying_price")
    private BigDecimal buyingPrice;

    @Column(nullable = false)
    private Integer quantity = 0;

    @Column
    private String sku;

    @Column
    private Boolean active = true;

    @OneToMany(mappedBy = "variantOption", cascade = CascadeType.ALL)
    private List<Variant> variants;

    // Constructors
    public VariantOption() {}

    public VariantOption(UUID id, String title, Gallery image, Product product, BigDecimal salePrice, BigDecimal comparePrice, BigDecimal buyingPrice, Integer quantity, String sku, Boolean active, List<Variant> variants) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.product = product;
        this.salePrice = salePrice;
        this.comparePrice = comparePrice;
        this.buyingPrice = buyingPrice;
        this.quantity = quantity;
        this.sku = sku;
        this.active = active;
        this.variants = variants;
    }

    // Getters and Setters
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

    public Gallery getImage() {
        return image;
    }

    public void setImage(Gallery image) {
        this.image = image;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
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

    public List<Variant> getVariants() {
        return variants;
    }

    public void setVariants(List<Variant> variants) {
        this.variants = variants;
    }
}