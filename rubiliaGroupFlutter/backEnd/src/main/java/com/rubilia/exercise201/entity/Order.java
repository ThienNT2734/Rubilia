package com.rubilia.exercise201.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnore;

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

    @Column(name = "account_id")
    private UUID accountId;

    @Column(name = "account_name", length = 255)
    private String accountName;

    @Column(name = "account_email", length = 255)
    private String accountEmail;

    @Column(name = "payment_status", length = 50)
    private String paymentStatus;

    // --- CONSTRUCTORS (Viết tay) ---
    public Order() {}

    public Order(String id, Long orderNumber, BigDecimal totalPrice, String paymentStatus) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.totalPrice = totalPrice;
        this.paymentStatus = paymentStatus;
    }

    // --- GETTERS AND SETTERS (Viết tay - Đây là phần quan trọng nhất để sửa lỗi) ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Long getOrderNumber() { return orderNumber; }
    public void setOrderNumber(Long orderNumber) { this.orderNumber = orderNumber; }

    public Coupon getCoupon() { return coupon; }
    public void setCoupon(Coupon coupon) { this.coupon = coupon; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public OrderStatus getOrderStatus() { return orderStatus; }
    public void setOrderStatus(OrderStatus orderStatus) { this.orderStatus = orderStatus; }

    public Date getOrderApprovedAt() { return orderApprovedAt; }
    public void setOrderApprovedAt(Date orderApprovedAt) { this.orderApprovedAt = orderApprovedAt; }

    public Date getOrderDeliveredCarrierDate() { return orderDeliveredCarrierDate; }
    public void setOrderDeliveredCarrierDate(Date orderDeliveredCarrierDate) { this.orderDeliveredCarrierDate = orderDeliveredCarrierDate; }

    public Date getOrderDeliveredCustomerDate() { return orderDeliveredCustomerDate; }
    public void setOrderDeliveredCustomerDate(Date orderDeliveredCustomerDate) { this.orderDeliveredCustomerDate = orderDeliveredCustomerDate; }

    public Date getCreated_at() { return created_at; }
    public void setCreated_at(Date created_at) { this.created_at = created_at; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public StaffAccount getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(StaffAccount updatedBy) { this.updatedBy = updatedBy; }

    public List<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }

    public UUID getAccountId() { return accountId; }
    public void setAccountId(UUID accountId) { this.accountId = accountId; }

    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }

    public String getAccountEmail() { return accountEmail; }
    public void setAccountEmail(String accountEmail) { this.accountEmail = accountEmail; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
}