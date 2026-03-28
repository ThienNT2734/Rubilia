package com.rubilia.exercise201.entity;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;

@Entity
@Table(name = "coupons")
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "discount_value", nullable = false)
    private BigDecimal discountValue;

    @Column(name = "discount_type", nullable = false, length = 50)
    private String discountType;

    @Column(name = "times_used", nullable = false)
    private BigDecimal timesUsed = BigDecimal.ZERO;

    @Column(name = "max_usage")
    private BigDecimal maxUsage;

    @Column(name = "order_amount_limit")
    private BigDecimal orderAmountLimit;

    @Column(name = "coupon_start_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date couponStartDate;

    @Column(name = "coupon_end_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date couponEndDate;

    @OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL)
    private List<ProductCoupon> productCoupons;

    // Constructors
    public Coupon() {}

    public Coupon(UUID id, String code, BigDecimal discountValue, String discountType, BigDecimal timesUsed, BigDecimal maxUsage, BigDecimal orderAmountLimit, Date couponStartDate, Date couponEndDate, List<ProductCoupon> productCoupons) {
        this.id = id;
        this.code = code;
        this.discountValue = discountValue;
        this.discountType = discountType;
        this.timesUsed = timesUsed;
        this.maxUsage = maxUsage;
        this.orderAmountLimit = orderAmountLimit;
        this.couponStartDate = couponStartDate;
        this.couponEndDate = couponEndDate;
        this.productCoupons = productCoupons;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getTimesUsed() {
        return timesUsed;
    }

    public void setTimesUsed(BigDecimal timesUsed) {
        this.timesUsed = timesUsed;
    }

    public BigDecimal getMaxUsage() {
        return maxUsage;
    }

    public void setMaxUsage(BigDecimal maxUsage) {
        this.maxUsage = maxUsage;
    }

    public BigDecimal getOrderAmountLimit() {
        return orderAmountLimit;
    }

    public void setOrderAmountLimit(BigDecimal orderAmountLimit) {
        this.orderAmountLimit = orderAmountLimit;
    }

    public Date getCouponStartDate() {
        return couponStartDate;
    }

    public void setCouponStartDate(Date couponStartDate) {
        this.couponStartDate = couponStartDate;
    }

    public Date getCouponEndDate() {
        return couponEndDate;
    }

    public void setCouponEndDate(Date couponEndDate) {
        this.couponEndDate = couponEndDate;
    }

    public List<ProductCoupon> getProductCoupons() {
        return productCoupons;
    }

    public void setProductCoupons(List<ProductCoupon> productCoupons) {
        this.productCoupons = productCoupons;
    }
}