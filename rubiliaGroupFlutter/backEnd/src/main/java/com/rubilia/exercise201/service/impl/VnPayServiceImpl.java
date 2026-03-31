package com.rubilia.exercise201.service.impl;

import com.rubilia.exercise201.service.VnPayService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;

@Service
public class VnPayServiceImpl implements VnPayService {

    @Value("${vnpay.url}")
    private String vnpUrl;

    @Value("${vnpay.return-url}")
    private String vnpReturnUrl;

    @Value("${vnpay.tmn-code}")
    private String vnpTmnCode;

    @Value("${vnpay.hash-secret}")
    private String vnpHashSecret;

    @Override
    public String createPaymentUrl(String orderId, long amount, String paymentType, String ipAddress) {
        if (vnpTmnCode == null || vnpTmnCode.isBlank() || vnpHashSecret == null || vnpHashSecret.isBlank()) {
            throw new IllegalStateException("VNPAY configuration missing TMN code or hash secret.");
        }

        Map<String, String> params = new TreeMap<>();
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", vnpTmnCode);
        params.put("vnp_Amount", String.valueOf(amount * 100));
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", orderId);
        params.put("vnp_OrderInfo", "Thanh toan don hang " + orderId);
        params.put("vnp_OrderType", "other");
        params.put("vnp_Locale", "vn");
        params.put("vnp_ReturnUrl", vnpReturnUrl);
        params.put("vnp_IpAddr", ipAddress);
        params.put("vnp_CreateDate", new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date()));
        params.put("vnp_SecureHashType", "SHA512");
        if (paymentType != null && paymentType.equalsIgnoreCase("QR")) {
            params.put("vnp_PayType", "QR");
        }

        String query = buildQuery(params);
        String secureHash = hmacSHA512(vnpHashSecret, query);
        return vnpUrl + "?" + query + "&vnp_SecureHash=" + secureHash;
    }

    @Override
    public boolean validateSecureHash(Map<String, String> queryParams) {
        Map<String, String> filteredParams = new TreeMap<>();
        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value == null || value.isEmpty()) {
                continue;
            }
            if ("vnp_SecureHash".equals(key) || "vnp_SecureHashType".equals(key)) {
                continue;
            }
            filteredParams.put(key, value);
        }

        String query = buildQuery(filteredParams);
        String expectedHash = hmacSHA512(vnpHashSecret, query);
        String actualHash = queryParams.get("vnp_SecureHash");
        return expectedHash.equalsIgnoreCase(actualHash);
    }

    private String buildQuery(Map<String, String> params) {
        StringJoiner joiner = new StringJoiner("&");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            joiner.add(urlEncode(entry.getKey()) + "=" + urlEncode(entry.getValue()));
        }
        return joiner.toString();
    }

    private String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name())
                    .replace("+", "%20")
                    .replace("%21", "!")
                    .replace("%27", "'")
                    .replace("%28", "(")
                    .replace("%29", ")");
        } catch (Exception e) {
            throw new RuntimeException("URL encode failed", e);
        }
    }

    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac.init(secretKey);
            byte[] bytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hash = new StringBuilder();
            for (byte b : bytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hash.append('0');
                }
                hash.append(hex);
            }
            return hash.toString();
        } catch (Exception e) {
            throw new RuntimeException("Unable to generate secure hash", e);
        }
    }
}
