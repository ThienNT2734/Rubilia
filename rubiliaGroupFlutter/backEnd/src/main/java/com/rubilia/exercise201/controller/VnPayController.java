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

@Controller
@RequestMapping("/api/vnpay")
public class VnPayController {

    @Autowired
    private VnPayService vnPayService;

    @Autowired
    private OrderService orderService;

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
                    long expectedAmount = order.getTotalPrice().multiply(java.math.BigDecimal.valueOf(100)).longValue();
                    if (expectedAmount != request.getAmount().longValue() * 100) {
                        return ResponseEntity.badRequest().body("Số tiền gửi lên không khớp với đơn hàng.");
                    }

                    String ipAddress = httpRequest.getRemoteAddr();
                    String paymentType = request.getPaymentType() != null ? request.getPaymentType() : "WEB";
                    String paymentUrl = vnPayService.createPaymentUrl(orderId, request.getAmount().longValue(), paymentType, ipAddress);
                    Map<String, String> response = new HashMap<>();
                    response.put("paymentUrl", paymentUrl);
                    if (paymentType.equalsIgnoreCase("QR")) {
                        response.put("qrData", paymentUrl);
                    }
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> ResponseEntity.badRequest().body("Đơn hàng không tồn tại."));
    }

    @GetMapping("/return")
    public RedirectView vnpayReturn(@RequestParam Map<String, String> queryParams) {
        boolean valid = vnPayService.validateSecureHash(queryParams);
        if (!valid) {
            return new RedirectView("http://localhost:3000/checkout?status=failed&message=Invalid+Signature");
        }

        String responseCode = queryParams.get("vnp_ResponseCode");
        String txnRef = queryParams.get("vnp_TxnRef");
        String amountStr = queryParams.get("vnp_Amount");
        if (txnRef == null || txnRef.isEmpty()) {
            return new RedirectView("http://localhost:3000/checkout?status=failed&message=Missing+vnp_TxnRef");
        }

        return orderService.findById(txnRef)
                .map(order -> {
                    long expectedAmount = order.getTotalPrice().multiply(java.math.BigDecimal.valueOf(100)).longValue();
                    long returnedAmount;
                    try {
                        returnedAmount = Long.parseLong(amountStr);
                    } catch (NumberFormatException e) {
                        return new RedirectView("http://localhost:3000/checkout?status=failed&orderId=" + order.getId() + "&message=Invalid+amount+returned+from+VNPAY");
                    }

                    if (returnedAmount != expectedAmount) {
                        return new RedirectView("http://localhost:3000/checkout?status=failed&orderId=" + order.getId() + "&message=Số+tiền+thanh+toán+không+khớp");
                    }

                    String finalStatus;
                    if ("00".equals(responseCode)) {
                        if ("PAID".equalsIgnoreCase(order.getPaymentStatus())) {
                            return new RedirectView("http://localhost:3000/checkout?status=success&orderId=" + order.getId() + "&message=Đơn+hàng+đã+được+thanh+toán+trước+đó");
                        }
                        finalStatus = "PAID";
                        orderService.updatePaymentStatus(txnRef, finalStatus);
                    } else {
                        finalStatus = "FAILED";
                        orderService.updatePaymentStatus(txnRef, finalStatus);
                    }
                    return new RedirectView("http://localhost:3000/checkout?status=" + ("PAID".equals(finalStatus) ? "success" : "failed") + "&orderId=" + order.getId());
                })
                .orElseGet(() -> new RedirectView("http://localhost:3000/checkout?status=failed&message=Đơn+hàng+không+tồn+tại"));
    }
}
