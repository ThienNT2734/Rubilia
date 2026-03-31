package com.rubilia.exercise201.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rubilia.exercise201.database.ProductDatabaseHandler;
import com.rubilia.exercise201.entity.Category;
import com.rubilia.exercise201.entity.Gallery;
import com.rubilia.exercise201.entity.Product;
import com.rubilia.exercise201.entity.ProductCategory;
import com.rubilia.exercise201.entity.ProductDisplayInfo;
import com.rubilia.exercise201.entity.StaffAccount;
import com.rubilia.exercise201.repository.CategoryRepository;
import com.rubilia.exercise201.repository.GalleryRepository;
import com.rubilia.exercise201.repository.ProductCategoryRepository;
import com.rubilia.exercise201.repository.ProductRepository;
import com.rubilia.exercise201.repository.ProductShippingInfoRepository;
import com.rubilia.exercise201.repository.StaffAccountRepository;
import com.rubilia.exercise201.repository.VariantOptionRepository;
import com.rubilia.exercise201.repository.OrderItemRepository;
import com.rubilia.exercise201.repository.CardItemRepository;
import com.rubilia.exercise201.repository.ProductSupplierRepository;
import com.rubilia.exercise201.repository.ProductTagRepository;
import com.rubilia.exercise201.service.ProductService;
import com.rubilia.exercise201.mapper.ProductMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ProductServiceImpl implements ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private StaffAccountRepository staffAccountRepository;

    @Autowired
    private GalleryRepository galleryRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private ProductShippingInfoRepository productShippingInfoRepository;

    @Autowired
    private VariantOptionRepository variantOptionRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CardItemRepository cardItemRepository;

    @Autowired
    private ProductSupplierRepository productSupplierRepository;

    @Autowired
    private ProductTagRepository productTagRepository;

    @Autowired
    private ProductDatabaseHandler productDatabaseHandler;

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    private final ObjectMapper objectMapper;

    public ProductServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(UUID id) {
        return productRepository.findById(id);
    }

    @Override
    @Transactional
    public ResponseEntity<?> save(JsonNode productJson, UUID staffId) {
        try {
            logger.info("Starting save for new product with staffId: {}", staffId);
            if (productJson == null) {
                throw new IllegalArgumentException("Product data cannot be null");
            }

            // Kiểm tra xem productJson có phải là JsonNode hợp lệ không
            if (!productJson.isObject()) {
                throw new IllegalArgumentException("Product data must be a JSON object");
            }

            // Log dữ liệu đầu vào đầy đủ
            logger.debug("Received product JSON: {}", productJson.toString());
            logger.debug("productName: {}", productJson.has("productName") ? productJson.get("productName").asText() : "null");
            logger.debug("salePrice: {}", productJson.has("salePrice") ? productJson.get("salePrice").asText() : "null");
            logger.debug("comparePrice: {}", productJson.has("comparePrice") ? productJson.get("comparePrice").asText() : "null");
            logger.debug("quantity: {}", productJson.has("quantity") ? productJson.get("quantity").asText() : "null");
            logger.debug("shortDescription: {}", productJson.has("shortDescription") ? productJson.get("shortDescription").asText() : "null");
            logger.debug("productDescription: {}", productJson.has("productDescription") ? productJson.get("productDescription").asText() : "null");
            logger.debug("idCategories: {}", productJson.has("idCategories") ? productJson.get("idCategories").toString() : "null");
            logger.debug("images: {}", productJson.has("images") ? productJson.get("images").toString() : "null");
            logger.debug("displayAreas: {}", productJson.has("displayAreas") ? productJson.get("displayAreas").toString() : "null");
            logger.debug("salesCount: {}", productJson.has("salesCount") ? productJson.get("salesCount").asText() : "null");
            logger.debug("rating: {}", productJson.has("rating") ? productJson.get("rating").asText() : "null");

            Product product = ProductMapper.mapJsonNodeToProduct(productJson);
            logger.debug("Product after mapping: {}", product);

            StaffAccount staff = staffAccountRepository.findById(staffId)
                    .orElseThrow(() -> new RuntimeException("Staff not found with ID: " + staffId));
            product.setCreatedBy(staff);
            product.setUpdatedBy(staff);
            product.setCreatedAt(new Date());
            product.setUpdatedAt(new Date());
            logger.debug("Staff assigned: createdBy={}, updatedBy={}", product.getCreatedBy(), product.getUpdatedBy());

            // Lưu danh mục
            List<UUID> idCategoryList;
            try {
                idCategoryList = productJson.has("idCategories") && !productJson.get("idCategories").isNull()
                    ? objectMapper.readValue(productJson.get("idCategories").traverse(), new TypeReference<List<UUID>>() {})
                    : new ArrayList<>();
            } catch (Exception e) {
                logger.error("Error parsing idCategories: ", e);
                throw new IllegalArgumentException("Invalid idCategories format: " + e.getMessage(), e);
            }

            logger.debug("idCategories after parsing: {}", idCategoryList);

            List<Category> categoryList = new ArrayList<>();
            for (UUID idCategory : idCategoryList) {
                Optional<Category> category = categoryRepository.findById(idCategory);
                if (!category.isPresent()) {
                    throw new IllegalArgumentException("Category with ID " + idCategory + " does not exist");
                }
                categoryList.add(category.get());
            }
            List<ProductCategory> productCategoryList = new ArrayList<>();
            for (Category category : categoryList) {
                ProductCategory productCategory = new ProductCategory();
                productCategory.setCategory(category);
                productCategory.setProduct(product);
                productCategoryList.add(productCategory);
            }
            product.setProductCategories(productCategoryList);
            logger.debug("Product Categories prepared: {}", productCategoryList);

            // Chuẩn bị ảnh
            List<String> imageList;
            try {
                imageList = productJson.has("images") && !productJson.get("images").isNull()
                    ? objectMapper.readValue(productJson.get("images").traverse(), new TypeReference<List<String>>() {})
                    : new ArrayList<>();
            } catch (Exception e) {
                logger.error("Error parsing images: ", e);
                throw new IllegalArgumentException("Invalid images format: " + e.getMessage(), e);
            }

            logger.debug("Images after parsing: {}", imageList);

            List<Gallery> galleries = new ArrayList<>();
            for (String imageBase64 : imageList) {
                if (imageBase64 == null || imageBase64.trim().isEmpty()) {
                    logger.warn("Skipping empty or null image: {}", imageBase64);
                    continue; // Bỏ qua nếu imageBase64 không hợp lệ
                }

                String imagePath;
                if (imageBase64.startsWith("/uploads/")) {
                    imagePath = imageBase64;
                } else {
                    String base64Data = imageBase64.contains(",") ? imageBase64.split(",")[1] : imageBase64;
                    try {
                        // Kiểm tra tính hợp lệ của Base64
                        Base64.getDecoder().decode(base64Data);
                    } catch (IllegalArgumentException e) {
                        logger.error("Invalid Base64 image data: {}", imageBase64, e);
                        throw new IllegalArgumentException("Invalid Base64 image data: " + e.getMessage(), e);
                    }
                    String fileName = UUID.randomUUID() + ".jpg";
                    Path filePath = Paths.get(uploadDir, fileName).toAbsolutePath().normalize();
                    Files.createDirectories(filePath.getParent());
                    Files.write(filePath, Base64.getDecoder().decode(base64Data));
                    imagePath = "/uploads/" + fileName;
                }
                Gallery gallery = new Gallery();
                gallery.setProduct(product);
                gallery.setImage(imagePath);
                gallery.setPlaceholder("placeholder-value");
                gallery.setCreatedAt(new Date());
                gallery.setUpdatedAt(new Date());
                galleries.add(gallery);
            }
            product.setGalleries(galleries);
            logger.debug("Galleries prepared: {}", galleries);

            // Chuẩn bị thông tin khu vực hiển thị
            List<ProductDisplayInfo> displayInfos = new ArrayList<>();
            if (productJson.has("displayAreas")) {
                List<String> displayAreas;
                try {
                    displayAreas = objectMapper.readValue(productJson.get("displayAreas").traverse(), new TypeReference<List<String>>() {});
                } catch (Exception e) {
                    logger.error("Error parsing displayAreas: ", e);
                    throw new IllegalArgumentException("Invalid displayAreas format: " + e.getMessage(), e);
                }

                logger.debug("Display Areas after parsing (before removing duplicates): {}", displayAreas);

                // Loại bỏ trùng lặp trong displayAreas để tránh vi phạm ràng buộc UNIQUE (product_id, display_area)
                List<String> uniqueDisplayAreas = new ArrayList<>(new HashSet<>(displayAreas));
                logger.debug("Display Areas after removing duplicates: {}", uniqueDisplayAreas);

                for (String area : uniqueDisplayAreas) {
                    if (area == null || area.trim().isEmpty()) {
                        logger.warn("Skipping empty or null display area: {}", area);
                        continue; // Bỏ qua nếu area không hợp lệ
                    }

                    ProductDisplayInfo displayInfo = new ProductDisplayInfo();
                    displayInfo.setProduct(product);
                    displayInfo.setDisplayArea(area);
                    try {
                        if (productJson.has("salesCount") && productJson.get("salesCount").isNumber()) {
                            double salesCountValue = productJson.get("salesCount").asDouble();
                            if (salesCountValue < 0) {
                                throw new IllegalArgumentException("salesCount cannot be negative");
                            }
                            displayInfo.setSalesCount(BigDecimal.valueOf(salesCountValue));
                        } else {
                            displayInfo.setSalesCount(BigDecimal.ZERO);
                        }
                    } catch (Exception e) {
                        logger.warn("Invalid salesCount format, defaulting to 0: {}", productJson.get("salesCount").asText(), e);
                        displayInfo.setSalesCount(BigDecimal.ZERO);
                    }
                    try {
                        if (productJson.has("rating") && productJson.get("rating").isNumber()) {
                            double ratingValue = productJson.get("rating").asDouble();
                            if (ratingValue < 0 || ratingValue > 5) {
                                throw new IllegalArgumentException("rating must be between 0 and 5");
                            }
                            displayInfo.setRating(BigDecimal.valueOf(ratingValue));
                        } else {
                            displayInfo.setRating(BigDecimal.ZERO);
                        }
                    } catch (Exception e) {
                        logger.warn("Invalid rating format, defaulting to 0: {}", productJson.get("rating").asText(), e);
                        displayInfo.setRating(BigDecimal.ZERO);
                    }
                    displayInfos.add(displayInfo);
                }
            }
            product.setDisplayInfos(displayInfos);
            logger.debug("Display Infos prepared: {}", displayInfos);

            // Kiểm tra dữ liệu trước khi lưu
            if (product.getProductCategories() == null) {
                logger.warn("ProductCategories is null, setting to empty list");
                product.setProductCategories(new ArrayList<>());
            }
            if (product.getGalleries() == null) {
                logger.warn("Galleries is null, setting to empty list");
                product.setGalleries(new ArrayList<>());
            }
            if (product.getDisplayInfos() == null) {
                logger.warn("DisplayInfos is null, setting to empty list");
                product.setDisplayInfos(new ArrayList<>());
            }

            // Sử dụng ProductDatabaseHandler để lưu sản phẩm
            logger.debug("Calling ProductDatabaseHandler to save product...");
            Product savedProduct = productDatabaseHandler.saveProduct(product);
            logger.debug("Product saved successfully: {}", savedProduct);

            return ResponseEntity.ok(savedProduct);
        } catch (Exception e) {
            logger.error("Error in save method: ", e);
            return ResponseEntity.status(500).body("Lỗi khi lưu sản phẩm: " + e.getMessage() + "; Stack trace: " + getStackTraceAsString(e));
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> update(UUID productId, JsonNode productJson, UUID staffId) {
        try {
            logger.info("Starting update for product ID: {}", productId);
            if (productJson == null) {
                throw new IllegalArgumentException("Product data cannot be null");
            }

            Product existingProduct = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

            StaffAccount staff = staffAccountRepository.findById(staffId)
                    .orElseThrow(() -> new RuntimeException("Staff not found with ID: " + staffId));

            // Cập nhật thông tin sản phẩm
            logger.debug("Updating product fields for ID: {}", productId);
            ProductMapper.updateProductFromJsonNode(existingProduct, productJson);
            existingProduct.setUpdatedBy(staff);
            existingProduct.setUpdatedAt(new Date());

            // Cập nhật danh mục
            logger.debug("Clearing existing product categories for product ID: {}", productId);
            existingProduct.getProductCategories().clear();
            List<UUID> idCategoryList;
            try {
                idCategoryList = productJson.has("idCategories") && !productJson.get("idCategories").isNull()
                    ? objectMapper.readValue(productJson.get("idCategories").traverse(), new TypeReference<List<UUID>>() {})
                    : new ArrayList<>();
            } catch (Exception e) {
                logger.error("Error parsing idCategories: ", e);
                throw new IllegalArgumentException("Invalid idCategories format: " + e.getMessage(), e);
            }

            List<ProductCategory> newCategories = new ArrayList<>();
            for (UUID idCategory : idCategoryList) {
                Category category = categoryRepository.findById(idCategory)
                        .orElseThrow(() -> new RuntimeException("Category not found: " + idCategory));
                ProductCategory pc = new ProductCategory();
                pc.setProduct(existingProduct);
                pc.setCategory(category);
                newCategories.add(pc);
            }
            existingProduct.getProductCategories().addAll(newCategories);

            // Cập nhật ảnh nếu có
            if (productJson.has("images")) {
                logger.debug("Clearing existing galleries for product ID: {}", productId);
                existingProduct.getGalleries().clear();
                List<String> imageList;
                try {
                    imageList = objectMapper.readValue(productJson.get("images").traverse(), new TypeReference<List<String>>() {});
                } catch (Exception e) {
                    logger.error("Error parsing images: ", e);
                    throw new IllegalArgumentException("Invalid images format: " + e.getMessage(), e);
                }

                List<Gallery> galleries = new ArrayList<>();
                for (String imageBase64 : imageList) {
                    if (imageBase64 == null || imageBase64.trim().isEmpty()) {
                        continue;
                    }

                    String imagePath;
                    if (imageBase64.startsWith("/uploads/")) {
                        imagePath = imageBase64;
                    } else {
                        String base64Data = imageBase64.contains(",") ? imageBase64.split(",")[1] : imageBase64;
                        String fileName = UUID.randomUUID() + ".jpg";
                        Path filePath = Paths.get(uploadDir, fileName).toAbsolutePath().normalize();
                        Files.createDirectories(filePath.getParent());
                        Files.write(filePath, Base64.getDecoder().decode(base64Data));
                        imagePath = "/uploads/" + fileName;
                    }

                    Gallery gallery = new Gallery();
                    gallery.setProduct(existingProduct);
                    gallery.setImage(imagePath);
                    gallery.setPlaceholder("placeholder-value");
                    gallery.setCreatedAt(new Date());
                    gallery.setUpdatedAt(new Date());
                    galleries.add(gallery);
                }
                existingProduct.getGalleries().addAll(galleries);
            }

            // Cập nhật thông tin khu vực hiển thị (chỉ xóa nếu có displayAreas trong dữ liệu gửi lên)
            logger.debug("Updating display infos for product ID: {}", productId);
            if (productJson.has("displayAreas")) {
                deleteProductDisplayInfosByProductId(productId);
                existingProduct.getDisplayInfos().clear();

                List<ProductDisplayInfo> newDisplayInfos = new ArrayList<>();
                List<String> displayAreas;
                try {
                    displayAreas = objectMapper.readValue(productJson.get("displayAreas").traverse(), new TypeReference<List<String>>() {});
                } catch (Exception e) {
                    logger.error("Error parsing displayAreas: ", e);
                    throw new IllegalArgumentException("Invalid displayAreas format: " + e.getMessage(), e);
                }

                List<String> uniqueDisplayAreas = new ArrayList<>(new HashSet<>(displayAreas));
                for (String area : uniqueDisplayAreas) {
                    if (area == null || area.trim().isEmpty()) {
                        continue;
                    }

                    ProductDisplayInfo displayInfo = new ProductDisplayInfo();
                    displayInfo.setProduct(existingProduct);
                    displayInfo.setDisplayArea(area);

                    try {
                        if (productJson.has("salesCount") && productJson.get("salesCount").isNumber()) {
                            double salesCountValue = productJson.get("salesCount").asDouble();
                            if (salesCountValue < 0) {
                                throw new IllegalArgumentException("salesCount cannot be negative");
                            }
                            displayInfo.setSalesCount(BigDecimal.valueOf(salesCountValue));
                        } else {
                            displayInfo.setSalesCount(BigDecimal.ZERO);
                        }
                    } catch (Exception e) {
                        logger.warn("Invalid salesCount format, defaulting to 0: {}", productJson.get("salesCount").asText(), e);
                        displayInfo.setSalesCount(BigDecimal.ZERO);
                    }
                    try {
                        if (productJson.has("rating") && productJson.get("rating").isNumber()) {
                            double ratingValue = productJson.get("rating").asDouble();
                            if (ratingValue < 0 || ratingValue > 5) {
                                throw new IllegalArgumentException("rating must be between 0 and 5");
                            }
                            displayInfo.setRating(BigDecimal.valueOf(ratingValue));
                        } else {
                            displayInfo.setRating(BigDecimal.ZERO);
                        }
                    } catch (Exception e) {
                        logger.warn("Invalid rating format, defaulting to 0: {}", productJson.get("rating").asText(), e);
                        displayInfo.setRating(BigDecimal.ZERO);
                    }

                    newDisplayInfos.add(displayInfo);
                }
                existingProduct.getDisplayInfos().addAll(newDisplayInfos);
            } else {
                // Nếu không có displayAreas trong dữ liệu gửi lên, giữ nguyên displayInfos
                // Cập nhật salesCount và rating cho các displayInfos hiện có
                for (ProductDisplayInfo displayInfo : existingProduct.getDisplayInfos()) {
                    try {
                        if (productJson.has("salesCount") && productJson.get("salesCount").isNumber()) {
                            double salesCountValue = productJson.get("salesCount").asDouble();
                            if (salesCountValue < 0) {
                                throw new IllegalArgumentException("salesCount cannot be negative");
                            }
                            displayInfo.setSalesCount(BigDecimal.valueOf(salesCountValue));
                        }
                    } catch (Exception e) {
                        logger.warn("Invalid salesCount format, defaulting to 0: {}", productJson.get("salesCount").asText(), e);
                        displayInfo.setSalesCount(BigDecimal.ZERO);
                    }
                    try {
                        if (productJson.has("rating") && productJson.get("rating").isNumber()) {
                            double ratingValue = productJson.get("rating").asDouble();
                            if (ratingValue < 0 || ratingValue > 5) {
                                throw new IllegalArgumentException("rating must be between 0 and 5");
                            }
                            displayInfo.setRating(BigDecimal.valueOf(ratingValue));
                        }
                    } catch (Exception e) {
                        logger.warn("Invalid rating format, defaulting to 0: {}", productJson.get("rating").asText(), e);
                        displayInfo.setRating(BigDecimal.ZERO);
                    }
                }
            }

            // Lưu tất cả thay đổi một lần duy nhất
            logger.debug("Saving updated product ID: {}", productId);
            productRepository.save(existingProduct);

            // Lấy lại sản phẩm để trả về với danh sách galleries
            Product savedProduct = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm vừa cập nhật: " + productId));
            logger.info("Successfully updated product ID: {}", productId);
            return ResponseEntity.ok(savedProduct);
        } catch (Exception e) {
            logger.error("Error updating product with id {}: ", productId, e);
            throw new RuntimeException("Cập nhật thất bại: " + (e.getMessage() != null ? e.getMessage() : "Lỗi không xác định"), e);
        }
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ResponseEntity<?> deleteProduct(UUID productId) {
        logger.info("Deleting product with id: {}", productId);
        try {
            Optional<Product> productOptional = productRepository.findById(productId);
            if (!productOptional.isPresent()) {
                logger.warn("Product not found with id: {}", productId);
                return ResponseEntity.status(404).body("Sản phẩm không tồn tại với ID: " + productId);
            }

            Product product = productOptional.get();
            logger.debug("Deleting product with id: {}", productId);
            productRepository.delete(product);

            return ResponseEntity.ok("Xóa thành công!");
        } catch (Exception e) {
            logger.error("Error deleting product with id {}: ", productId, e);
            return ResponseEntity.status(500).body("Xóa thất bại: " + e.getMessage() + "; Stack trace: " + getStackTraceAsString(e));
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteOrderItemsByProductId(UUID productId) {
        logger.debug("Deleting order items for product ID: {}", productId);
        orderItemRepository.deleteByProductId(productId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteCardItemsByProductId(UUID productId) {
        logger.debug("Deleting card items for product ID: {}", productId);
        cardItemRepository.deleteByProductId(productId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteProductSuppliersByProductId(UUID productId) {
        logger.debug("Deleting product suppliers for product ID: {}", productId);
        productSupplierRepository.deleteByProductId(productId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteProductTagsByProductId(UUID productId) {
        logger.debug("Deleting product tags for product ID: {}", productId);
        productTagRepository.deleteByProductId(productId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteProductShippingInfoByProductId(UUID productId) {
        logger.debug("Deleting product shipping info for product ID: {}", productId);
        productShippingInfoRepository.deleteByProductId(productId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteVariantOptionsByImageId(UUID imageId) {
        logger.debug("Deleting variant options for image ID: {}", imageId);
        variantOptionRepository.deleteByImageId(imageId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteProductCategoriesByProductId(UUID productId) {
        logger.debug("Deleting product categories for product ID: {}", productId);
        productCategoryRepository.deleteAllByProductId(productId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteGalleriesByProductId(UUID productId) {
        logger.debug("Deleting galleries for product ID: {}", productId);
        galleryRepository.deleteAllByProductId(productId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteProductDisplayInfosByProductId(UUID productId) {
        logger.debug("Deleting product display infos for product ID: {}", productId);
        productRepository.deleteDisplayInfosByProductId(productId);
    }

    @Override
    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return productRepository.findAll();
        }
        return productRepository.findByProductNameContainingIgnoreCase(keyword);
    }

    @Override
    public List<Product> getProductsByDisplayArea(String displayArea) {
        return productRepository.findByDisplayArea(displayArea);
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