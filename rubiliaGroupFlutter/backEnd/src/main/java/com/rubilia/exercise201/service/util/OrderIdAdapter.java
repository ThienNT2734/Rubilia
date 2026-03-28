package com.rubilia.exercise201.service.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OrderIdAdapter {

    public UUID toUUID(String orderId) {
        if (orderId == null) {
            return null;
        }
        if (isCustomFormat(orderId)) {
            // Trả về null để báo hiệu chuỗi tùy chỉnh, xử lý trực tiếp ở service
            return null;
        }
        try {
            return UUID.fromString(orderId);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public String toString(UUID orderId) {
        return orderId != null ? orderId.toString() : null;
    }

    public boolean isCustomFormat(String orderId) {
        return orderId != null && orderId.startsWith("ORDER");
    }
}