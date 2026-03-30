package com.rubilia.exercise201.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rubilia.exercise201.dto.SentimentAnalysisResult;
import com.rubilia.exercise201.entity.SentimentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
public class GeminiService {

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.model:gemini-1.5-flash}")
    private String model;

    public GeminiService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public SentimentAnalysisResult analyzeComment(String productName, String content) {
        if (apiKey == null || apiKey.isBlank()) {
            return new SentimentAnalysisResult(SentimentType.UNKNOWN, null, "Gemini API key chưa được cấu hình");
        }

        String prompt = buildPrompt(productName, content);
        ObjectNode payload = objectMapper.createObjectNode();
        ObjectNode contentNode = payload.putArray("contents")
                .addObject()
                .putArray("parts")
                .addObject();
        contentNode.put("text", prompt);

        ObjectNode generationConfig = payload.putObject("generationConfig");
        generationConfig.put("temperature", 0.2);

        String url = String.format(
                "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s",
                model,
                apiKey
        );

        try {
            String body = objectMapper.writeValueAsString(payload);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                return new SentimentAnalysisResult(SentimentType.UNKNOWN, null,
                        "Gemini lỗi HTTP: " + response.statusCode());
            }

            String text = extractText(response.body());
            return parseResult(text);
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return new SentimentAnalysisResult(SentimentType.UNKNOWN, null, "Lỗi gọi Gemini: " + e.getMessage());
        }
    }

    private String buildPrompt(String productName, String content) {
        String safeProduct = productName == null || productName.isBlank() ? "(không rõ)" : productName;
        String safeContent = content == null ? "" : content;
        return "Bạn là hệ thống phân tích cảm xúc. Hãy phân tích cảm xúc của khách hàng từ bình luận về sản phẩm. "
                + "Chỉ trả về JSON với các trường: sentiment (POSITIVE|NEGATIVE|NEUTRAL), score (0..1), explanation. "
                + "Không thêm văn bản khác.\n"
                + "Sản phẩm: " + safeProduct + "\n"
                + "Bình luận: " + safeContent;
    }

    private String extractText(String responseBody) throws JsonProcessingException {
        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode textNode = root.path("candidates").path(0)
                .path("content").path("parts").path(0).path("text");
        return textNode.isTextual() ? textNode.asText() : "";
    }

    private SentimentAnalysisResult parseResult(String text) throws JsonProcessingException {
        if (text == null || text.isBlank()) {
            return new SentimentAnalysisResult(SentimentType.UNKNOWN, null, "Gemini trả về rỗng");
        }

        String cleaned = text.trim();
        if (cleaned.startsWith("```")) {
            cleaned = cleaned.replaceAll("^```(json)?", "");
            cleaned = cleaned.replaceAll("```$", "");
            cleaned = cleaned.trim();
        }

        JsonNode node = objectMapper.readTree(cleaned);
        String sentimentText = node.path("sentiment").asText("UNKNOWN").toUpperCase();
        SentimentType sentiment;
        try {
            sentiment = SentimentType.valueOf(sentimentText);
        } catch (IllegalArgumentException ex) {
            sentiment = SentimentType.UNKNOWN;
        }

        Double score = node.path("score").isNumber() ? node.path("score").asDouble() : null;
        String explanation = node.path("explanation").asText(null);
        return new SentimentAnalysisResult(sentiment, score, explanation);
    }
}
