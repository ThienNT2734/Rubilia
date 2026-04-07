package com.rubilia.exercise201.controller;

import com.rubilia.exercise201.dto.VnPayPaymentRequest;
import com.rubilia.exercise201.entity.Order;
import com.rubilia.exercise201.service.OrderService;
import com.rubilia.exercise201.service.VnPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.view.RedirectView;
import java.util.HashMap;
import java.util.Map;
import java.math.BigDecimal;

@Controller
@RequestMapping("/api/vnpay")
public class VnPayController {

    @Autowired
    private VnPayService vnPayService;

    @Autowired
    private OrderService orderService;

    // ĐỊNH NGHĨA DOMAIN ĐỂ DỄ BẢO TRÌ
    private final String FRONTEND_URL = "https://rubilia.store";

    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<?> createPayment(@RequestBody VnPayPaymentRequest request, HttpServletRequest httpRequest) {
        String orderId = request.getOrderId();
        if (orderId == null || orderId.isEmpty() || request.getAmount() == null) {
            return ResponseEntity.badRequest().body("orderId và amount là bắt buộc.");
        }

        return orderService.findById(orderId)
                .map(order -> {
                    if (!"PENDING".equalsIgnoreCase(order.getPaymentStatus())) {
                        return ResponseEntity.badRequest().body("Đơn hàng chỉ có thể thanh toán khi đang ở trạng thái PENDING.");
                    }
                    
                    // VNPAY tính theo đơn vị VNĐ * 100
                    long expectedAmount = order.getTotalPrice().multiply(BigDecimal.valueOf(100)).longValue();
                    if (expectedAmount != request.getAmount().longValue() * 100) {
                        return ResponseEntity.badRequest().body("Số tiền gửi lên không khớp với đơn hàng.");
                    }

                    String ipAddress = httpRequest.getRemoteAddr();
                    String paymentType = request.getPaymentType() != null ? request.getPaymentType() : "WEB";
                    String paymentUrl = vnPayService.createPaymentUrl(orderId, request.getAmount().longValue(), paymentType, ipAddress);
                    
                    Map<String, String> response = new HashMap<>();
                    response.put("paymentUrl", paymentUrl);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> ResponseEntity.badRequest().body("Đơn hàng không tồn tại."));
    }

    @GetMapping("/return")
    public RedirectView vnpayReturn(@RequestParam Map<String, String> queryParams) {
        // 1. Kiểm tra chữ ký bảo mật
        boolean valid = vnPayService.validateSecureHash(queryParams);
        if (!valid) {
            return new RedirectView(FRONTEND_URL + "/checkout?status=failed&message=Invalid+Signature");
        }

        String responseCode = queryParams.get("vnp_ResponseCode");
        String txnRef = queryParams.get("vnp_TxnRef"); // Đây chính là OrderID
        String amountStr = queryParams.get("vnp_Amount");

        if (txnRef == null || txnRef.isEmpty()) {
            return new RedirectView(FRONTEND_URL + "/checkout?status=failed&message=Missing+vnp_TxnRef");
        }

        return orderService.findById(txnRef)
                .map(order -> {
                    // 2. Kiểm tra số tiền trả về từ VNPAY có khớp với DB không
                    long expectedAmount = order.getTotalPrice().multiply(BigDecimal.valueOf(100)).longValue();
                    long returnedAmount;
                    try {
                        returnedAmount = Long.parseLong(amountStr);
                    } catch (NumberFormatException e) {
                        return new RedirectView(FRONTEND_URL + "/checkout?status=failed&orderId=" + order.getId() + "&message=Invalid+Amount");
                    }

                    if (returnedAmount != expectedAmount) {
                        return new RedirectView(FRONTEND_URL + "/checkout?status=failed&orderId=" + order.getId() + "&message=Amount+Mismatch");
                    }

                    // 3. Cập nhật trạng thái dựa trên ResponseCode
                    if ("00".equals(responseCode)) {
                        // Nếu đã PAID rồi thì không cập nhật lại, chỉ redirect về success
                        if (!"PAID".equalsIgnoreCase(order.getPaymentStatus())) {
                            orderService.updatePaymentStatus(txnRef, "PAID");
                        }
                        return new RedirectView(FRONTEND_URL + "/checkout?status=success&orderId=" + order.getId());
                    } else {
                        orderService.updatePaymentStatus(txnRef, "FAILED");
                        return new RedirectView(FRONTEND_URL + "/checkout?status=failed&orderId=" + order.getId() + "&code=" + responseCode);
                    }
                })
                .orElseGet(() -> new RedirectView(FRONTEND_URL + "/checkout?status=failed&message=Order+Not+Found"));
    }
}