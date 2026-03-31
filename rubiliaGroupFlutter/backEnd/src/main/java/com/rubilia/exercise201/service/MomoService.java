package com.rubilia.exercise201.service;

public interface MomoService {
    String createPaymentUrl(String orderId, long amount, String paymentType, String ipAddress);
}
