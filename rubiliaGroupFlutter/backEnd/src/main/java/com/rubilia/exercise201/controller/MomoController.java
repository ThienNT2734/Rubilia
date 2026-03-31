package com.rubilia.exercise201.controller;

import com.rubilia.exercise201.dto.MomoPaymentRequest;
import com.rubilia.exercise201.entity.Order;
import com.rubilia.exercise201.service.MomoService;
import com.rubilia.exercise201.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/momo")
public class MomoController {

    private static final Logger logger = LoggerFactory.getLogger(MomoController.class);

    @Autowired
    private MomoService momoService;

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
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
                        String paymentUrl = momoService.createPaymentUrl(orderId, request.getAmount().longValue(), ipAddress);
                        Map<String, String> response = new HashMap<>();
                        response.put("paymentUrl", paymentUrl);
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
    public ResponseEntity<?> momoReturn(@RequestParam Map<String, String> queryParams) {
        String orderId = queryParams.get("orderId");
        String resultCode = queryParams.get("resultCode");

        if (orderId == null || orderId.isEmpty()) {
            return ResponseEntity.badRequest().body("Missing orderId.");
        }

        return orderService.findById(orderId)
                .map(order -> {
                    String finalStatus;
                    if ("0".equals(resultCode)) {
                        if ("PAID".equalsIgnoreCase(order.getPaymentStatus())) {
                            return (ResponseEntity<?>) ResponseEntity.ok(Map.of("message", "Đơn hàng đã được thanh toán trước đó.", "orderId", order.getId()));
                        }
                        finalStatus = "PAID";
                        orderService.updatePaymentStatus(orderId, finalStatus);
                    } else {
                        finalStatus = "FAILED";
                        orderService.updatePaymentStatus(orderId, finalStatus);
                    }
                    return ResponseEntity.ok(Map.of(
                            "orderId", order.getId(),
                            "paymentStatus", finalStatus,
                            "resultCode", resultCode
                    ));
                })
                .orElseGet(() -> (ResponseEntity<?>) ResponseEntity.badRequest().body("Đơn hàng không tồn tại."));
    }
}
