package com.rubilia.exercise201.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @Column(length = 50)
    private String id;

    @Column(name = "order_number")
    private Long orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    @JsonIgnore
    private Coupon coupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    @JsonIgnore
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_status_id")
    @JsonIgnore
    private OrderStatus orderStatus;

    @Column(name = "order_approved_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date orderApprovedAt;

    @Column(name = "order_delivered_carrier_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date orderDeliveredCarrierDate;

    @Column(name = "order_delivered_customer_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date orderDeliveredCustomerDate;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created_at;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    @JsonIgnore
    private StaffAccount updatedBy;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "coupon_code", length = 50)
    private String couponCode;

    // Thêm các trường mới
    @Column(name = "account_id")
    private UUID accountId;

    @Column(name = "account_name", length = 255)
    private String accountName;

    @Column(name = "account_email", length = 255)
    private String accountEmail;

    @Column(name = "payment_status", length = 50)
    private String paymentStatus;
}