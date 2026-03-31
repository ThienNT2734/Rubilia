package com.rubilia.exercise201.dto;

import java.math.BigDecimal;

public class MomoPaymentRequest {
    private String orderId;
    private BigDecimal amount;
    private String paymentType;

    public MomoPaymentRequest() {}

    public MomoPaymentRequest(String orderId, BigDecimal amount, String paymentType) {
        this.orderId = orderId;
        this.amount = amount;
        this.paymentType = paymentType;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }
}
