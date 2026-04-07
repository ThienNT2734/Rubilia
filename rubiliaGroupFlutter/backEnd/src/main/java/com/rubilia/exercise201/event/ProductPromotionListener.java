package com.rubilia.exercise201.event;

import com.rubilia.exercise201.entity.Product;
import com.rubilia.exercise201.service.CustomerNotificationService;
import com.rubilia.exercise201.service.EmailService;
import com.rubilia.exercise201.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductPromotionListener implements ApplicationListener<ProductPromotionEvent> {

    @Autowired
    private CustomerNotificationService notificationService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private CustomerRepository customerRepository;

    @Override
    @Async
    public void onApplicationEvent(ProductPromotionEvent event) {
        Product product = event.getProduct();

        // Chỉ gửi thông báo khi có discount_percentage > 0
        if (product.getDiscountPercentage() != null &&
            product.getDiscountPercentage().compareTo(BigDecimal.ZERO) > 0) {

            String discountStr = product.getDiscountPercentage().setScale(0, BigDecimal.ROUND_HALF_UP).toString();
            String title = "🔥 GIẢM GIÁ " + discountStr + "% - " + product.getProductName();
            String content = "Sản phẩm bạn quan tâm hiện đang giảm giá " + discountStr + "%! " +
                           "Giá gốc: " + product.getComparePrice() + " đ. Nhấp vào để xem ngay!";

            String imageUrl = product.getGalleries() != null && !product.getGalleries().isEmpty()
                    ? "https://rubilia.store" + product.getGalleries().get(0).getImage()
                    : null;

            // Gửi thông báo đến TẤT CẢ khách hàng
            notificationService.sendBulkPromotionNotification(
                    title,
                    content,
                    product.getId(),
                    imageUrl
            );
            
            // GỬI EMAIL KHUYẾN MÃI ĐẾN TẤT CẢ KHÁCH HÀNG
            BigDecimal originalPrice = product.getComparePrice();
            BigDecimal discountPercent = product.getDiscountPercentage();
            BigDecimal finalPrice = originalPrice.multiply(BigDecimal.ONE.subtract(discountPercent.divide(new BigDecimal("100")))).setScale(0, BigDecimal.ROUND_HALF_UP);
            
            // Lấy danh sách email tất cả khách hàng active
            List<String> allCustomerEmails = customerRepository.findAllByActiveTrue()
                    .stream()
                    .map(customer -> customer.getEmail())
                    .collect(Collectors.toList());
            
            // Gửi email hàng loạt
            emailService.sendBulkPromotionEmail(
                    allCustomerEmails,
                    product.getProductName(),
                    discountPercent,
                    originalPrice,
                    finalPrice,
                    imageUrl,
                    product.getId()
            );
        }
    }
}
