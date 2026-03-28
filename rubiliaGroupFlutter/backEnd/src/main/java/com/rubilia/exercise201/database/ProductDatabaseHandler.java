package com.rubilia.exercise201.database;

import com.rubilia.exercise201.entity.Product;
import com.rubilia.exercise201.repository.ProductRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductDatabaseHandler {
    private static final Logger logger = LoggerFactory.getLogger(ProductDatabaseHandler.class);

    @Autowired
    private ProductRepository productRepository;

    public Product saveProduct(Product product) throws Exception {
        try {
            // Kiểm tra trùng lặp slug
            String originalSlug = product.getSlug();
            String slug = originalSlug;
            int suffix = 1;
            while (productRepository.findBySlug(slug).isPresent()) {
                slug = originalSlug + "-" + suffix;
                suffix++;
            }
            product.setSlug(slug);
            logger.debug("Final slug for product: {}", slug);

            // Log dữ liệu trước khi lưu
            logger.debug("Saving product to database: {}", product);
            logger.debug("Product Categories: {}", product.getProductCategories());
            logger.debug("Galleries: {}", product.getGalleries());
            logger.debug("Display Infos: {}", product.getDisplayInfos());

            // Lưu sản phẩm
            Product savedProduct = productRepository.save(product);
            logger.info("Successfully saved product with ID: {}", savedProduct.getId());
            return savedProduct;
        } catch (ConstraintViolationException e) {
            logger.error("Constraint violation while saving product: ", e);
            throw new Exception("Failed to save product due to database constraint violation: " + e.getMessage() + "; Constraint: " + e.getConstraintName() + "; Stack trace: " + getStackTraceAsString(e));
        } catch (Exception e) {
            logger.error("Error saving product to database: ", e);
            throw new Exception("Failed to save product to database: " + e.getMessage() + "; Stack trace: " + getStackTraceAsString(e));
        }
    }

    // Phương thức phụ để lấy stack trace dưới dạng chuỗi
    private String getStackTraceAsString(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        sb.append(throwable.toString()).append("\n");
        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append("\tat ").append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}