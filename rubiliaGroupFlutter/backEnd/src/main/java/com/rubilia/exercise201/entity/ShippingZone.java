package com.rubilia.exercise201.entity;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "shipping_zones")
public class ShippingZone {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String display_name;

    @Column
    private Boolean active = false;

    @Column(name = "free_shipping")
    private Boolean freeShipping = false;

    @Column(length = 64, name = "rate_type")
    @Enumerated(EnumType.STRING)
    private RateType rateType;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created_at;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated_at;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    @JsonIgnore
    private StaffAccount createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    @JsonIgnore
    private StaffAccount updatedBy;

    @OneToMany(mappedBy = "shippingZone", cascade = CascadeType.ALL)
    private List<ShippingRate> shippingRates;

    public enum RateType {
        price
    }

    // Constructors
    public ShippingZone() {}

    public ShippingZone(UUID id, String name, String display_name, Boolean active, Boolean freeShipping, RateType rateType, Date created_at, Date updated_at, StaffAccount createdBy, StaffAccount updatedBy, List<ShippingRate> shippingRates) {
        this.id = id;
        this.name = name;
        this.display_name = display_name;
        this.active = active;
        this.freeShipping = freeShipping;
        this.rateType = rateType;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.shippingRates = shippingRates;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getFreeShipping() {
        return freeShipping;
    }

    public void setFreeShipping(Boolean freeShipping) {
        this.freeShipping = freeShipping;
    }

    public RateType getRateType() {
        return rateType;
    }

    public void setRateType(RateType rateType) {
        this.rateType = rateType;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public StaffAccount getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(StaffAccount createdBy) {
        this.createdBy = createdBy;
    }

    public StaffAccount getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(StaffAccount updatedBy) {
        this.updatedBy = updatedBy;
    }

    public List<ShippingRate> getShippingRates() {
        return shippingRates;
    }

    public void setShippingRates(List<ShippingRate> shippingRates) {
        this.shippingRates = shippingRates;
    }
}