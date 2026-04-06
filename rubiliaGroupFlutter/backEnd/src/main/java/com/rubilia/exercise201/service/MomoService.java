package com.rubilia.exercise201.service;

import java.util.Map;

public interface MomoService {
    String createPaymentUrl(String orderId, long amount, String paymentType, String ipAddress);
    boolean validateSignature(Map<String, String> queryParams);
}
