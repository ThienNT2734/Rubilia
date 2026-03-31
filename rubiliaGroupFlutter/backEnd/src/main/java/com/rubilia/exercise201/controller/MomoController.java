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
import org.springframework.web.servlet.view.RedirectView;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/momo")
public class MomoController {

    private static final Logger logger = LoggerFactory.getLogger(MomoController.class);

    @Autowired
    private MomoService momoService;

    @Autowired
    private OrderService orderService;

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
                            return (ResponseEntity<?>) ResponseEntity.badRequest().body("Đơn hàng chỉ có thể thanh toán khi đang ở trạng thái PENDING.");
                        }
                        long expectedAmount = order.getTotalPrice().longValue();
                        if (expectedAmount != request.getAmount().longValue()) {
                            return (ResponseEntity<?>) ResponseEntity.badRequest().body("Số tiền gửi lên không khớp với đơn hàng.");
                        }

                        String ipAddress = httpRequest.getRemoteAddr();
                        String paymentType = request.getPaymentType();
                        String paymentUrl = momoService.createPaymentUrl(orderId, request.getAmount().longValue(), paymentType, ipAddress);
                        Map<String, String> response = new HashMap<>();
                        response.put("paymentUrl", paymentUrl);
                        response.put("paymentType", paymentType == null ? "captureWallet" : paymentType);
                        return ResponseEntity.ok(response);
                    })
                    .orElseGet(() -> (ResponseEntity<?>) ResponseEntity.badRequest().body("Đơn hàng không tồn tại."));
        } catch (Exception e) {
            logger.error("MoMo payment creation failed", e);
            String message = e.getMessage();
            if (e.getCause() != null && e.getCause().getMessage() != null) {
                message += " | root cause: " + e.getCause().getMessage();
            }
            return ResponseEntity.status(500).body(Map.of("message", message));
        }
    }

    @GetMapping("/return")
    public RedirectView momoReturn(@RequestParam Map<String, String> queryParams) {
        String orderId = queryParams.get("orderId");
        String resultCode = queryParams.get("resultCode");

        if (orderId == null || orderId.isEmpty()) {
            return new RedirectView("http://localhost:3000/checkout?status=failed&message=Missing+orderId");
        }

        return orderService.findById(orderId)
                .map(order -> {
                    String finalStatus;
                    if ("0".equals(resultCode)) {
                        if ("PAID".equalsIgnoreCase(order.getPaymentStatus())) {
                            return new RedirectView("http://localhost:3000/checkout?status=success&orderId=" + order.getId() + "&message=Đơn+hàng+đã+được+thanh+toán+trước+đó");
                        }
                        finalStatus = "PAID";
                        orderService.updatePaymentStatus(orderId, finalStatus);
                    } else {
                        finalStatus = "FAILED";
                        orderService.updatePaymentStatus(orderId, finalStatus);
                    }
                    return new RedirectView("http://localhost:3000/checkout?status=" + ("PAID".equals(finalStatus) ? "success" : "failed") + "&orderId=" + order.getId());
                })
                .orElseGet(() -> new RedirectView("http://localhost:3000/checkout?status=failed&message=Đơn+hàng+không+tồn+tại"));
    }
}
