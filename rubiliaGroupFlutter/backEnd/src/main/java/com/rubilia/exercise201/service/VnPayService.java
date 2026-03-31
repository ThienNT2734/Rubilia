package com.rubilia.exercise201.service;

import java.util.Map;

public interface VnPayService {
    String createPaymentUrl(String orderId, long amount, String paymentType, String ipAddress);
    boolean validateSecureHash(Map<String, String> queryParams);
}
