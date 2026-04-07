package com.rubilia.exercise201.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendPromotionEmail(String toEmail, String productName, BigDecimal discountPercent, BigDecimal originalPrice, BigDecimal finalPrice, String productImageUrl, UUID productId) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("Rubilia Store <noreply@rubilia.store>");
            helper.setTo(toEmail);
            helper.setSubject("🔥 GIẢM GIÁ " + discountPercent.setScale(0, BigDecimal.ROUND_HALF_UP) + "% - " + productName);

            // Tạo email HTML thủ công không dùng Thymeleaf
            StringBuilder html = new StringBuilder();
            html.append("<div style=\"max-width:600px;margin:0 auto;font-family:Arial,sans-serif;\">");
            html.append("<div style=\"background:linear-gradient(90deg,#ff4d4f,#ff7875);padding:20px;text-align:center;color:white;\">");
            html.append("<h1>🔥 KHUYẾN MÃI ĐẶC BIỆT!</h1>");
            html.append("</div>");
            html.append("<div style=\"padding:30px;background:#fff;\">");
            html.append("<h2 style=\"color:#333;margin-bottom:20px;\">").append(productName).append("</h2>");
            if (productImageUrl != null) {
                html.append("<img src=\"").append(productImageUrl).append("\" style=\"width:100%;max-width:400px;border-radius:8px;margin-bottom:20px;\" alt=\"Product\" />");
            }
            html.append("<div style=\"font-size:18px;margin-bottom:15px;\">");
            html.append("<span style=\"color:#999;text-decoration:line-through;\">Giá gốc: ").append(originalPrice.toPlainString()).append(" đ</span>");
            html.append("</div>");
            html.append("<div style=\"font-size:28px;font-weight:bold;color:#ff4d4f;margin-bottom:20px;\">");
            html.append("GIẢM CÒN: ").append(finalPrice.toPlainString()).append(" đ");
            html.append("</div>");
            html.append("<div style=\"background:#fff7f7;padding:12px;border-radius:6px;margin-bottom:25px;\">");
            html.append("<strong>Giảm ").append(discountPercent.setScale(0, BigDecimal.ROUND_HALF_UP)).append("%</strong> chỉ trong thời gian hạn chế!");
            html.append("</div>");
            html.append("<a href=\"https://rubilia.store/product/").append(productId).append("\" style=\"display:block;background:#ff4d4f;color:white;text-align:center;padding:15px;border-radius:8px;text-decoration:none;font-weight:bold;\">");
            html.append("👉 XEM SẢN PHẨM NGAY");
            html.append("</a>");
            html.append("</div></div>");

            helper.setText(html.toString(), true);
            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Async
    public void sendBulkPromotionEmail(List<String> emails, String productName, BigDecimal discountPercent, BigDecimal originalPrice, BigDecimal finalPrice, String productImageUrl, UUID productId) {
        for (String email : emails) {
            sendPromotionEmail(email, productName, discountPercent, originalPrice, finalPrice, productImageUrl, productId);
        }
    }
}
