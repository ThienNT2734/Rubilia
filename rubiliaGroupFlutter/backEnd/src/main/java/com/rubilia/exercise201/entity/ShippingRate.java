package com.rubilia.exercise201.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "shipping_rates")
public class ShippingRate {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_zone_id", nullable = false)
    @JsonIgnore
    private ShippingZone shippingZone;

    @Column(name = "weight_unit", length = 10)
    @Enumerated(EnumType.STRING)
    private WeightUnit weightUnit;

    @Column(name = "min_value", nullable = false)
    private BigDecimal minValue = BigDecimal.ZERO;

    @Column(name = "max_value")
    private BigDecimal maxValue;

    @Column(name = "no_max")
    private Boolean noMax = true;

    @Column(nullable = false)
    private BigDecimal price = BigDecimal.ZERO;

    public enum WeightUnit {
        g,
        kg
    }

    // Constructors
    public ShippingRate() {}

    public ShippingRate(UUID id, ShippingZone shippingZone, WeightUnit weightUnit, BigDecimal minValue, BigDecimal maxValue, Boolean noMax, BigDecimal price) {
        this.id = id;
        this.shippingZone = shippingZone;
        this.weightUnit = weightUnit;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.noMax = noMax;
        this.price = price;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ShippingZone getShippingZone() {
        return shippingZone;
    }

    public void setShippingZone(ShippingZone shippingZone) {
        this.shippingZone = shippingZone;
    }

    public WeightUnit getWeightUnit() {
        return weightUnit;
    }

    public void setWeightUnit(WeightUnit weightUnit) {
        this.weightUnit = weightUnit;
    }

    public BigDecimal getMinValue() {
        return minValue;
    }

    public void setMinValue(BigDecimal minValue) {
        this.minValue = minValue;
    }

    public BigDecimal getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(BigDecimal maxValue) {
        this.maxValue = maxValue;
    }

    public Boolean getNoMax() {
        return noMax;
    }

    public void setNoMax(Boolean noMax) {
        this.noMax = noMax;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}