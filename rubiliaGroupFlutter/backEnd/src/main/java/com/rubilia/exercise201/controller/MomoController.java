package com.rubilia.exercise201.controller;

import com.rubilia.exercise201.dto.MomoPaymentRequest;
import com.rubilia.exercise201.entity.Order;
import com.rubilia.exercise201.service.MomoService;
import com.rubilia.exercise201.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.web.servlet.view.RedirectView;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/momo")
@RequiredArgsConstructor
public class MomoController {

    private static final Logger logger = LoggerFactory.getLogger(MomoController.class);

    // ĐỊNH NGHĨA DOMAIN Ở ĐÂY ĐỂ DỄ QUẢN LÝ
    private final String FRONTEND_URL = "https://rubilia.store";

    private final MomoService momoService;

    private final OrderService orderService;

    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<?> createPayment(@RequestBody MomoPaymentRequest request, HttpServletRequest httpRequest) {
        String orderId = request.getOrderId();
        if (orderId == null || orderId.isEmpty() || request.getAmount() == null) {
            return ResponseEntity.badRequest().body("orderId và amount là bắt buộc.");
        }

        try {
            return orderService.findById(orderId)
                    .map(order -> {
                        if (!"PENDING".equalsIgnoreCase(order.getPaymentStatus())) {
                            return ResponseEntity.badRequest().body("Đơn hàng chỉ có thể thanh toán khi đang ở trạng thái PENDING.");
                        }
                        long expectedAmount = order.getTotalPrice().longValue();
                        if (expectedAmount != request.getAmount().longValue()) {
                            return ResponseEntity.badRequest().body("Số tiền gửi lên không khớp với đơn hàng.");
                        }

                        String ipAddress = httpRequest.getRemoteAddr();
                        String paymentType = request.getPaymentType();
                        String paymentUrl = momoService.createPaymentUrl(orderId, request.getAmount().longValue(), paymentType, ipAddress);
                        Map<String, String> response = new HashMap<>();
                        response.put("paymentUrl", paymentUrl);
                        response.put("paymentType", paymentType == null ? "captureWallet" : paymentType);
                        return ResponseEntity.ok(response);
                    })
                    .orElseGet(() -> ResponseEntity.badRequest().body("Đơn hàng không tồn tại."));
        } catch (Exception e) {
            logger.error("MoMo payment creation failed", e);
            return ResponseEntity.status(500).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/return")
    public RedirectView momoReturn(@RequestParam Map<String, String> queryParams) {
        String orderId = queryParams.get("orderId");
        String amount = queryParams.get("amount");
        String resultCode = queryParams.get("resultCode");

        // 1. Kiểm tra orderId
        if (orderId == null || orderId.isEmpty()) {
            return new RedirectView(FRONTEND_URL + "/checkout?status=failed&message=Missing+orderId");
        }

        // 2. Validate Signature (Bảo mật)
        boolean isSignatureValid = momoService.validateSignature(queryParams);
        if (!isSignatureValid) {
            logger.error("MoMo return signature mismatch for orderId: {}", orderId);
            return new RedirectView(FRONTEND_URL + "/checkout?status=failed&message=Invalid+Signature");
        }

        return orderService.findById(orderId)
                .map(order -> {
                    // 3. Kiểm tra số tiền có khớp không
                    long expectedAmount = order.getTotalPrice().longValue();
                    if (Long.parseLong(amount) != expectedAmount) {
                        return new RedirectView(FRONTEND_URL + "/checkout?status=failed&orderId=" + order.getId() + "&message=Amount+mismatch");
                    }

                    // 4. Xử lý trạng thái thanh toán
                    String finalStatus;
                    if ("0".equals(resultCode)) {
                        finalStatus = "PAID";
                        orderService.updatePaymentStatus(orderId, finalStatus);
                        return new RedirectView(FRONTEND_URL + "/checkout?status=success&orderId=" + order.getId());
                    } else {
                        finalStatus = "FAILED";
                        orderService.updatePaymentStatus(orderId, finalStatus);
                        return new RedirectView(FRONTEND_URL + "/checkout?status=failed&orderId=" + order.getId());
                    }
                })
                .orElseGet(() -> new RedirectView(FRONTEND_URL + "/checkout?status=failed&message=Order+not+found"));
    }
}