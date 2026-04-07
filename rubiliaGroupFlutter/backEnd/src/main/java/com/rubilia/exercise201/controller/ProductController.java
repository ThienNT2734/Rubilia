package com.rubilia.exercise201.controller;

import com.rubilia.exercise201.dto.*;
import com.rubilia.exercise201.entity.*;
import com.rubilia.exercise201.event.ProductPromotionEvent;
import com.rubilia.exercise201.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        List<ProductDTO> productDTOs = products.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(productDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable UUID id) {
        return productService.getProductById(id)
                .map(product -> ResponseEntity.ok(convertToDTO(product)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/display-area/{area}")
    public ResponseEntity<List<ProductDTO>> getProductsByDisplayArea(@PathVariable String area) {
        List<Product> products = productService.getProductsByDisplayArea(area);
        List<ProductDTO> productDTOs = products.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(productDTOs);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createProduct(
            @RequestBody Map<String, Object> productData,
            Authentication authentication) {
        try {
            // Lấy staffId từ principal đã xác thực
            StaffAccount staff = (StaffAccount) authentication.getPrincipal();
            UUID staffId = staff.getId();
            // Chuyển đổi Map thành JsonNode
            JsonNode productJson = objectMapper.convertValue(productData, JsonNode.class);
            return productService.save(productJson, staffId);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating product: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> productData,
            @RequestParam UUID staffId) {
        try {
            JsonNode jsonNode = objectMapper.convertValue(productData, JsonNode.class);
            ResponseEntity<?> response = productService.update(id, jsonNode, staffId);

            // Nếu cập nhật thành công và có discount_percentage thì gửi thông báo khuyến mãi
            if (response.getStatusCode().is2xxSuccessful()) {
                productService.getProductById(id).ifPresent(product -> {
                    eventPublisher.publishEvent(new ProductPromotionEvent(this, product));
                });
            }

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi khi cập nhật sản phẩm: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteProduct(@PathVariable UUID id) {
        return productService.deleteProduct(id);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String keyword) {
        List<Product> products = productService.searchProducts(keyword);
        List<ProductDTO> productDTOs = products.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(productDTOs);
    }

private ProductDTO convertToDTO(Product product) {
    ProductDTO dto = new ProductDTO();
    dto.setId(product.getId());
    dto.setSlug(product.getSlug());
    dto.setProductName(product.getProductName());
    dto.setSku(product.getSku());
    dto.setPrice(product.getPrice());
    
    // --- SỬA LỖI CUỐI CÙNG ---
    // 1. TRUY XUẤT TRỰC TIẾP FIELD KHÔNG QUA GETTER
    // Bỏ qua tất cả getter/setter và logic Entity
    // Lấy trực tiếp discount_percentage từ database
    BigDecimal discount = product.getDiscountPercentage();
    if (discount == null) discount = BigDecimal.ZERO;
    
    dto.setDiscountPercentage(discount);
    dto.setIsOnPromotion(discount.compareTo(BigDecimal.ZERO) > 0);
    dto.setComparePrice(product.getComparePrice());
    dto.setSalePrice(null);
    // ----------------------

    dto.setPromotionStart(product.getPromotionStart());
    dto.setPromotionEnd(product.getPromotionEnd());
    dto.setBuyingPrice(product.getBuyingPrice());
    dto.setQuantity(product.getQuantity());
    dto.setShortDescription(product.getShortDescription());
    dto.setProductDescription(product.getProductDescription());
    dto.setProductType(product.getProductType() != null ? product.getProductType().toString() : null);
    dto.setPublished(product.getPublished());
    dto.setDisableOutOfStock(product.getDisableOutOfStock());
    dto.setNote(product.getNote());
    dto.setCreatedAt(product.getCreatedAt());
    dto.setUpdatedAt(product.getUpdatedAt());

        // Convert productCategories
        if (product.getProductCategories() != null) {
            List<ProductCategoryDTO> categoryDTOs = product.getProductCategories().stream().map(pc -> {
                ProductCategoryDTO pcDTO = new ProductCategoryDTO();
                pcDTO.setId(pc.getId());
                if (pc.getCategory() != null) {
                    CategoryDTO categoryDTO = new CategoryDTO();
                    categoryDTO.setId(pc.getCategory().getId());
                    categoryDTO.setCategoryName(pc.getCategory().getCategoryName());
                    categoryDTO.setCategoryDescription(pc.getCategory().getCategoryDescription());
                    categoryDTO.setCreatedAt(pc.getCategory().getCreatedAt());
                    categoryDTO.setUpdatedAt(pc.getCategory().getUpdatedAt());
                    pcDTO.setCategory(categoryDTO);
                }
                return pcDTO;
            }).collect(Collectors.toList());
            dto.setProductCategories(categoryDTOs);
        }

        // Convert shippingInfo
        if (product.getShippingInfo() != null) {
            ProductShippingInfoDTO shippingInfoDTO = new ProductShippingInfoDTO();
            shippingInfoDTO.setId(product.getShippingInfo().getId());
            shippingInfoDTO.setDimensionDepth(product.getShippingInfo().getDimensionDepth());
            shippingInfoDTO.setDimensionHeight(product.getShippingInfo().getDimensionHeight());
            shippingInfoDTO.setDimensionUnit(product.getShippingInfo().getDimensionUnit() != null ? product.getShippingInfo().getDimensionUnit().toString() : null);
            shippingInfoDTO.setDimensionWidth(product.getShippingInfo().getDimensionWidth());
            shippingInfoDTO.setVolume(product.getShippingInfo().getVolume());
            shippingInfoDTO.setVolumeUnit(product.getShippingInfo().getVolumeUnit() != null ? product.getShippingInfo().getVolumeUnit().toString() : null);
            shippingInfoDTO.setWeight(product.getShippingInfo().getWeight());
            shippingInfoDTO.setWeightUnit(product.getShippingInfo().getWeightUnit() != null ? product.getShippingInfo().getWeightUnit().toString() : null);
            dto.setShippingInfo(shippingInfoDTO);
        }

        // Convert galleries
        if (product.getGalleries() != null) {
            List<GalleryDTO> galleryDTOs = product.getGalleries().stream().map(g -> {
                GalleryDTO gDTO = new GalleryDTO();
                gDTO.setId(g.getId());
                gDTO.setImage(g.getImage());
                gDTO.setPlaceholder(g.getPlaceholder());
                gDTO.setIsThumbnail(g.getIsThumbnail());
                gDTO.setCreatedAt(g.getCreatedAt());
                gDTO.setUpdatedAt(g.getUpdatedAt());
                return gDTO;
            }).collect(Collectors.toList());
            dto.setGalleries(galleryDTOs);
        }

        // Convert displayInfos
        if (product.getDisplayInfos() != null) {
            List<ProductDisplayInfoDTO> displayInfoDTOs = product.getDisplayInfos().stream().map(di -> {
                ProductDisplayInfoDTO diDTO = new ProductDisplayInfoDTO();
                diDTO.setId(di.getId());
                diDTO.setDisplayArea(di.getDisplayArea());
                diDTO.setSalesCount(di.getSalesCount());
                diDTO.setRating(di.getRating());
                return diDTO;
            }).collect(Collectors.toList());
            dto.setDisplayInfos(displayInfoDTOs);
        }

        return dto;
    }
}