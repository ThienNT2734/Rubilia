package com.rubilia.exercise201.service.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Random;

@Component
public class OrderIdGenerator {

    private final Random random = new Random();

    public String generateCustomId(Long orderNumber, Date createdAt) {
        // Chuyển Date thành LocalDateTime
        LocalDateTime dateTime = createdAt.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        String day = String.format("%02d", dateTime.getDayOfMonth());
        String month = String.format("%02d", dateTime.getMonthValue());
        String year = String.valueOf(dateTime.getYear());
        String hours = String.format("%02d", dateTime.getHour());
        String minutes = String.format("%02d", dateTime.getMinute());
        // Thêm hậu tố ngẫu nhiên để tránh trùng lặp
        String randomSuffix = String.format("%03d", random.nextInt(1000));
        return String.format("ORDER%06d-%s%s%s-%sh%s-%s", orderNumber, day, month, year, hours, minutes, randomSuffix);
    }

    public String generateUuid() {
        return java.util.UUID.randomUUID().toString();
    }
}