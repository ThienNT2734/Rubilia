package com.rubilia.exercise201.service.impl;

import com.rubilia.exercise201.service.VnPayService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

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

        // 1. Khai báo tham số và dùng TreeMap để tự động sắp xếp theo Alphabet (A-Z)
        Map<String, String> vnp_Params = new TreeMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", vnpTmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount * 100)); // Số tiền x 100 theo quy định VNPAY
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", orderId);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang " + orderId);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnpReturnUrl);
        vnp_Params.put("vnp_IpAddr", ipAddress);
        
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        vnp_Params.put("vnp_CreateDate", formatter.format(cld.getTime()));

        // 2. Xây dựng 2 chuỗi khác nhau: Một để băm (Hash) và một để làm Query String (URL)
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<Map.Entry<String, String>> itr = vnp_Params.entrySet().iterator();

        while (itr.hasNext()) {
            Map.Entry<String, String> entry = itr.next();
            String fieldName = entry.getKey();
            String fieldValue = entry.getValue();

            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                // Build chuỗi Hash (KHÔNG URL Encode)
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(fieldValue);

                // Build chuỗi Query (CÓ URL Encode)
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        // 3. Tạo chữ ký bảo mật bằng HMAC-SHA512
        String vnp_SecureHash = hmacSHA512(vnpHashSecret, hashData.toString());
        
        // 4. Trả về URL hoàn chỉnh
        return vnpUrl + "?" + query.toString() + "&vnp_SecureHash=" + vnp_SecureHash;
    }

    @Override
    public boolean validateSecureHash(Map<String, String> queryParams) {
        String vnp_SecureHash = queryParams.get("vnp_SecureHash");
        
        // Loại bỏ các tham số không dùng để tính toán chữ ký khi nhận phản hồi
        Map<String, String> filteredParams = new TreeMap<>();
        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty() 
                && !entry.getKey().equals("vnp_SecureHash") 
                && !entry.getKey().equals("vnp_SecureHashType")) {
                filteredParams.put(entry.getKey(), entry.getValue());
            }
        }

        // Tạo lại chuỗi hash từ dữ liệu phản hồi
        StringBuilder hashData = new StringBuilder();
        Iterator<Map.Entry<String, String>> itr = filteredParams.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<String, String> entry = itr.next();
            hashData.append(entry.getKey()).append('=').append(entry.getValue());
            if (itr.hasNext()) {
                hashData.append('&');
            }
        }

        String checkSum = hmacSHA512(vnpHashSecret, hashData.toString());
        return checkSum.equalsIgnoreCase(vnp_SecureHash);
    }

    // Thuật toán băm chuẩn VNPAY v2.1.0
    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac.init(secretKey);
            byte[] bytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hash = new StringBuilder();
            for (byte b : bytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hash.append('0');
                hash.append(hex);
            }
            return hash.toString();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo mã băm bảo mật", e);
        }
    }
}