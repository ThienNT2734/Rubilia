package com.rubilia.exercise201.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rubilia.exercise201.service.MomoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class MomoServiceImpl implements MomoService {

    @Value("${momo.partner-code}")
    private String partnerCode;

    @Value("${momo.access-key}")
    private String accessKey;

    @Value("${momo.secret-key}")
    private String secretKey;

    @Value("${momo.return-url}")
    private String returnUrl;

    @Value("${momo.notify-url}")
    private String notifyUrl;

    @Value("${momo.endpoint}")
    private String momoEndpoint;

    private final ObjectMapper objectMapper;

    public MomoServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String createPaymentUrl(String orderId, long amount, String ipAddress) {
        if (partnerCode == null || partnerCode.isBlank() || accessKey == null || accessKey.isBlank() || secretKey == null || secretKey.isBlank()) {
            throw new IllegalStateException("MoMo configuration missing partner code, access key, or secret key.");
        }

        try {
            String requestId = orderId + "-" + System.currentTimeMillis();
            String orderInfo = "Thanh toan don hang " + orderId;
            String amountString = String.valueOf(amount);
            String extraData = "";
            String requestType = "captureWallet";

            String rawSignature = "accessKey=" + accessKey +
                    "&amount=" + amountString +
                    "&extraData=" + extraData +
                    "&ipnUrl=" + notifyUrl +
                    "&orderId=" + orderId +
                    "&orderInfo=" + orderInfo +
                    "&partnerCode=" + partnerCode +
                    "&redirectUrl=" + returnUrl +
                    "&requestId=" + requestId +
                    "&requestType=" + requestType;

            String signature = hmacSHA256(secretKey, rawSignature);

            Map<String, String> payload = new LinkedHashMap<>();
            payload.put("partnerCode", partnerCode);
            payload.put("accessKey", accessKey);
            payload.put("requestId", requestId);
            payload.put("amount", amountString);
            payload.put("orderId", orderId);
            payload.put("orderInfo", orderInfo);
            payload.put("redirectUrl", returnUrl);
            payload.put("ipnUrl", notifyUrl);
            payload.put("extraData", extraData);
            payload.put("requestType", requestType);
            payload.put("signature", signature);

            String jsonPayload = objectMapper.writeValueAsString(payload);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(momoEndpoint))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode responseJson = objectMapper.readTree(response.body());

            if (responseJson.has("resultCode") && responseJson.get("resultCode").asInt() == 0) {
                return responseJson.get("payUrl").asText();
            }

            throw new RuntimeException("MoMo request failed: " + response.body());
        } catch (Exception e) {
            throw new RuntimeException("Unable to create MoMo payment URL: " + e.getMessage(), e);
        }
    }

    private String hmacSHA256(String key, String data) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
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
