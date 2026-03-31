package com.rubilia.exercise201.dto;

import java.math.BigDecimal;

public class MomoPaymentRequest {
    private String orderId;
    private BigDecimal amount;

    public MomoPaymentRequest() {}

    public MomoPaymentRequest(String orderId, BigDecimal amount) {
        this.orderId = orderId;
        this.amount = amount;
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
}
