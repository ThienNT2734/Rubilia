package com.rubilia.exercise201.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.rubilia.exercise201.entity.Product;
import com.rubilia.exercise201.entity.Product.ProductType;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProductMapper {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public static Product mapJsonNodeToProduct(JsonNode productJson) {
        if (productJson == null) {
            throw new IllegalArgumentException("Product JSON cannot be null");
        }

        Product product = new Product();
        product.setProductName(productJson.has("productName") && !productJson.get("productName").isNull() ? productJson.get("productName").asText() : "");
        product.setSalePrice(productJson.has("salePrice") && productJson.get("salePrice").isNumber() ? BigDecimal.valueOf(productJson.get("salePrice").asDouble()) : BigDecimal.ZERO);
        product.setComparePrice(productJson.has("comparePrice") && productJson.get("comparePrice").isNumber() ? BigDecimal.valueOf(productJson.get("comparePrice").asDouble()) : BigDecimal.ZERO);
        product.setQuantity(productJson.has("quantity") && productJson.get("quantity").isNumber() ? productJson.get("quantity").asInt() : 0);
        product.setShortDescription(productJson.has("shortDescription") && !productJson.get("shortDescription").isNull() ? productJson.get("shortDescription").asText() : "");
        product.setProductDescription(productJson.has("productDescription") && !productJson.get("productDescription").isNull() ? productJson.get("productDescription").asText() : "");
        product.setSlug(productJson.has("slug") && !productJson.get("slug").isNull() ? productJson.get("slug").asText() : "unnamed-product");
        
        // Xử lý productType an toàn
        try {
            product.setProductType(productJson.has("productType") && !productJson.get("productType").isNull() ? ProductType.valueOf(productJson.get("productType").asText()) : ProductType.simple);
        } catch (IllegalArgumentException e) {
            product.setProductType(ProductType.simple);
        }

        product.setPublished(productJson.has("published") && !productJson.get("published").isNull() ? productJson.get("published").asBoolean() : false);
        product.setDisableOutOfStock(productJson.has("disableOutOfStock") && !productJson.get("disableOutOfStock").isNull() ? productJson.get("disableOutOfStock").asBoolean() : true);

        // Xử lý promotion
        product.setDiscountPercentage(productJson.has("discountPercentage") && productJson.get("discountPercentage").isNumber() ? BigDecimal.valueOf(productJson.get("discountPercentage").asDouble()) : BigDecimal.ZERO);
        product.setIsOnPromotion(productJson.has("isOnPromotion") && !productJson.get("isOnPromotion").isNull() ? productJson.get("isOnPromotion").asBoolean() : false);
        
        // Xử lý promotionStart
        try {
            product.setPromotionStart(productJson.has("promotionStart") && !productJson.get("promotionStart").isNull() ? dateFormat.parse(productJson.get("promotionStart").asText()) : null);
        } catch (Exception e) {
            product.setPromotionStart(null);
        }
        
        // Xử lý promotionEnd
        try {
            product.setPromotionEnd(productJson.has("promotionEnd") && !productJson.get("promotionEnd").isNull() ? dateFormat.parse(productJson.get("promotionEnd").asText()) : null);
        } catch (Exception e) {
            product.setPromotionEnd(null);
        }

        // Xử lý createdAt và updatedAt an toàn
        try {
            product.setCreatedAt(productJson.has("createdAt") && !productJson.get("createdAt").isNull() ? dateFormat.parse(productJson.get("createdAt").asText()) : new Date());
        } catch (Exception e) {
            product.setCreatedAt(new Date());
        }
        try {
            product.setUpdatedAt(productJson.has("updatedAt") && !productJson.get("updatedAt").isNull() ? dateFormat.parse(productJson.get("updatedAt").asText()) : new Date());
        } catch (Exception e) {
            product.setUpdatedAt(new Date());
        }

        return product;
    }

    public static void updateProductFromJsonNode(Product product, JsonNode productJson) {
        if (productJson == null) {
            throw new IllegalArgumentException("Product JSON cannot be null");
        }

        product.setProductName(productJson.has("productName") && !productJson.get("productName").isNull() ? productJson.get("productName").asText() : product.getProductName());
        product.setSalePrice(productJson.has("salePrice") && productJson.get("salePrice").isNumber() ? BigDecimal.valueOf(productJson.get("salePrice").asDouble()) : product.getSalePrice());
        product.setComparePrice(productJson.has("comparePrice") && productJson.get("comparePrice").isNumber() ? BigDecimal.valueOf(productJson.get("comparePrice").asDouble()) : product.getComparePrice());
        product.setQuantity(productJson.has("quantity") && productJson.get("quantity").isNumber() ? productJson.get("quantity").asInt() : product.getQuantity());
        product.setShortDescription(productJson.has("shortDescription") && !productJson.get("shortDescription").isNull() ? productJson.get("shortDescription").asText() : product.getShortDescription());
        product.setProductDescription(productJson.has("productDescription") && !productJson.get("productDescription").isNull() ? productJson.get("productDescription").asText() : product.getProductDescription());
        product.setSlug(productJson.has("slug") && !productJson.get("slug").isNull() ? productJson.get("slug").asText() : product.getSlug());
        
        // Xử lý productType an toàn
        try {
            product.setProductType(productJson.has("productType") && !productJson.get("productType").isNull() ? ProductType.valueOf(productJson.get("productType").asText()) : product.getProductType());
        } catch (IllegalArgumentException e) {
            product.setProductType(product.getProductType());
        }

        product.setPublished(productJson.has("published") && !productJson.get("published").isNull() ? productJson.get("published").asBoolean() : product.getPublished());
        product.setDisableOutOfStock(productJson.has("disableOutOfStock") && !productJson.get("disableOutOfStock").isNull() ? productJson.get("disableOutOfStock").asBoolean() : product.getDisableOutOfStock());

        // Xử lý promotion
        product.setDiscountPercentage(productJson.has("discountPercentage") && productJson.get("discountPercentage").isNumber() ? BigDecimal.valueOf(productJson.get("discountPercentage").asDouble()) : product.getDiscountPercentage());
        product.setIsOnPromotion(productJson.has("isOnPromotion") && !productJson.get("isOnPromotion").isNull() ? productJson.get("isOnPromotion").asBoolean() : product.getIsOnPromotion());
        
        // Xử lý promotionStart
        try {
            product.setPromotionStart(productJson.has("promotionStart") && !productJson.get("promotionStart").isNull() ? dateFormat.parse(productJson.get("promotionStart").asText()) : product.getPromotionStart());
        } catch (Exception e) {
            product.setPromotionStart(product.getPromotionStart());
        }
        
        // Xử lý promotionEnd
        try {
            product.setPromotionEnd(productJson.has("promotionEnd") && !productJson.get("promotionEnd").isNull() ? dateFormat.parse(productJson.get("promotionEnd").asText()) : product.getPromotionEnd());
        } catch (Exception e) {
            product.setPromotionEnd(product.getPromotionEnd());
        }
        try {
            product.setUpdatedAt(productJson.has("updatedAt") && !productJson.get("updatedAt").isNull() ? dateFormat.parse(productJson.get("updatedAt").asText()) : new Date());
        } catch (Exception e) {
            product.setUpdatedAt(new Date());
        }
    }
}