package com.rubilia.exercise201.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rubilia.exercise201.entity.*;
import com.rubilia.exercise201.repository.*;
import com.rubilia.exercise201.service.OrderService;
import com.rubilia.exercise201.service.util.OrderIdAdapter;
import com.rubilia.exercise201.service.util.OrderIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerAddressRepository customerAddressRepository;

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StaffAccountRepository staffAccountRepository;

    @Autowired
    private OrderIdGenerator orderIdGenerator;

    @Autowired
    private OrderIdAdapter orderIdAdapter;

    @Autowired
    private ObjectMapper objectMapper;

    // Phương thức trung gian để tìm hoặc tạo Customer
    private Customer findOrCreateCustomer(String firstName, String lastName, String email, String phone, String address) {
        // Tìm Customer theo email hoặc phone_number
        Optional<Customer> existingCustomer = customerRepository.findByEmail(email);
        if (existingCustomer.isEmpty()) {
            existingCustomer = customerRepository.findByPhoneNumber(phone);
        }

        if (existingCustomer.isPresent()) {
            Customer customer = existingCustomer.get();
            // Cập nhật thông tin nếu cần
            customer.setFirstName(firstName);
            customer.setLastName(lastName);
            customer.setAddress(address);
            customer.setUpdatedAt(new Date());
            return customerRepository.save(customer);
        } else {
            // Tạo Customer mới
            Customer newCustomer = new Customer();
            newCustomer.setFirstName(firstName);
            newCustomer.setLastName(lastName);
            newCustomer.setPhoneNumber(phone);
            newCustomer.setAddress(address);
            newCustomer.setActive(true);
            newCustomer.setRegisteredAt(new Date());
            newCustomer.setUpdatedAt(new Date());
            newCustomer.setUserName("guest_" + UUID.randomUUID().toString());
            newCustomer.setPasswordHash("guest_password");
            newCustomer.setEmail(email);
            return customerRepository.save(newCustomer);
        }
    }

    @Override
    public List<Order> findAll() {
        logger.info("Fetching all orders");
        return orderRepository.findAll();
    }

    @Override
    public Optional<Order> findById(String id) {
        logger.info("Fetching order with id: {}", id);
        return orderRepository.findById(id);
    }

    @Override
    public List<Order> findByCustomer(Customer customer) {
        logger.info("Fetching orders for customer with id: {}", customer.getId());
        return orderRepository.findByCustomer(customer);
    }

    @Override
    public List<Order> findByOrderStatus(OrderStatus orderStatus) {
        logger.info("Fetching orders with status: {}", orderStatus.getStatusName());
        return orderRepository.findByOrderStatus(orderStatus);
    }

    @Override
    public List<Order> findByCustomerAndOrderStatus(Customer customer, OrderStatus orderStatus) {
        logger.info("Fetching orders for customer id: {} with status: {}", customer.getId(), orderStatus.getStatusName());
        return orderRepository.findByCustomerAndOrderStatus(customer, orderStatus);
    }

    @Override
    public Order save(Order order) {
        logger.info("Saving order with id: {}", order.getId());
        return orderRepository.save(order);
    }

    @Override
    public void deleteById(String id) {
        logger.info("Deleting order with id: {}", id);
        orderRepository.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        logger.info("Checking if order exists with id: {}", id);
        return orderRepository.existsById(id);
    }

    @Override
    public ResponseEntity<?> checkout(Object orderData) {
        return ResponseEntity.status(500).body("Phương thức cũ không được sử dụng.");
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ResponseEntity<?> processCheckout(Object orderData) {
        logger.info("Processing new checkout");
        try {
            JsonNode orderJson = objectMapper.valueToTree(orderData);
            logger.debug("Received order data: {}", orderJson.toString());

            // 1. Lấy thông tin tài khoản hiện tại
            String accountEmail = null;
            String accountName = null;
            UUID accountId = null;
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserDetails) {
                String username = ((UserDetails) principal).getUsername();
                Customer account = customerRepository.findByUserName(username);
                if (account != null) {
                    accountEmail = account.getEmail();
                    accountName = account.getFirstName() + " " + account.getLastName();
                    accountId = account.getId();
                }
            }
            if (accountEmail == null) {
                logger.warn("No authenticated user found, proceeding with guest checkout");
            }

            // 2. Xử lý thông tin khách hàng
            JsonNode customerNode = orderJson.get("customer");
            if (customerNode == null) {
                logger.error("Customer information is missing");
                throw new IllegalArgumentException("Thông tin khách hàng không được để trống.");
            }
            logger.debug("Customer data: {}", customerNode.toString());

            String phone = customerNode.get("phone").asText();
            String email = customerNode.get("email").asText();
            String address = customerNode.get("address").asText();
            String[] nameParts = customerNode.get("name").asText().split(" ");
            String firstName = nameParts[0];
            String lastName = nameParts.length > 1 ? nameParts[1] : "Customer";

            // Tìm hoặc tạo Customer
            Customer customer = findOrCreateCustomer(firstName, lastName, email, phone, address);
            logger.info("Using customer with id: {}", customer.getId());

            // 3. Lưu địa chỉ giao hàng
            CustomerAddress customerAddress = new CustomerAddress();
            customerAddress.setCustomer(customer);
            customerAddress.setAddress_line1(address);
            customerAddress.setAddress_line2("");
            customerAddress.setCity("Nha Trang");
            customerAddress.setCountry("VN");
            customerAddress.setDial_code("+84");
            customerAddress.setPhone_number(phone);
            customerAddress.setPostal_code("700000");
            customerAddress = customerAddressRepository.save(customerAddress);
            logger.debug("Saved customer address with id: {}", customerAddress.getId());

            // 4. Tạo đơn hàng
            Order order = new Order();
            Date createdAt = new Date();
            Long orderCount = orderRepository.count();
            Long orderNumber = orderCount + 1;
            order.setOrderNumber(orderNumber);
            String orderId = orderIdGenerator.generateCustomId(orderNumber, createdAt);
            order.setId(orderId);
            order.setCreated_at(createdAt);
            order.setTotalPrice(BigDecimal.valueOf(orderJson.get("totalPrice").asDouble()));
            order.setCustomer(customer);
            order.setPaymentMethod(orderJson.get("paymentMethod").asText());
            order.setCouponCode(orderJson.has("couponCode") && !orderJson.get("couponCode").isNull() ? orderJson.get("couponCode").asText() : null);
            order.setAccountId(accountId);
            order.setAccountName(accountName);
            order.setAccountEmail(accountEmail);
            order.setPaymentStatus("PENDING");

            // Gán trạng thái Pending
            Optional<OrderStatus> orderStatusOptional = orderStatusRepository.findByStatusName("Pending");
            if (!orderStatusOptional.isPresent()) {
                logger.error("Order status 'Pending' not found");
                throw new IllegalStateException("Không tìm thấy trạng thái đơn hàng 'Pending'.");
            }
            order.setOrderStatus(orderStatusOptional.get());

            // Xử lý mã giảm giá
            if (order.getCouponCode() != null && !order.getCouponCode().isEmpty()) {
                Optional<Coupon> couponOptional = couponRepository.findByCode(order.getCouponCode());
                if (couponOptional.isPresent()) {
                    Coupon coupon = couponOptional.get();
                    order.setCoupon(coupon);
                    coupon.setTimesUsed(coupon.getTimesUsed().add(BigDecimal.ONE));
                    couponRepository.save(coupon);
                }
            }

            order = orderRepository.saveAndFlush(order);
            logger.info("Saved order with id: {}", orderId);

            // 5. Lưu các mục trong đơn hàng
            JsonNode cartItemsNode = orderJson.get("cartItems");
            if (cartItemsNode == null || !cartItemsNode.isArray()) {
                logger.error("Cart items are missing or invalid");
                throw new IllegalArgumentException("Danh sách sản phẩm không hợp lệ.");
            }

            List<OrderItem> orderItems = new ArrayList<>();
            for (JsonNode itemNode : cartItemsNode) {
                UUID productId = UUID.fromString(itemNode.get("productId").asText());
                int quantity = itemNode.get("quantity").asInt();
                BigDecimal price = BigDecimal.valueOf(itemNode.get("price").asDouble());

                Optional<Product> productOptional = productRepository.findById(productId);
                if (!productOptional.isPresent()) {
                    logger.error("Product not found: {}", productId);
                    throw new IllegalArgumentException("Sản phẩm không tồn tại: " + productId);
                }
                Product product = productOptional.get();
                if (product.getQuantity() < quantity) {
                    logger.error("Insufficient stock for product: {}", product.getProductName());
                    throw new IllegalArgumentException("Sản phẩm " + product.getProductName() + " không đủ hàng trong kho.");
                }

                product.setQuantity(product.getQuantity() - quantity);
                productRepository.save(product);

                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProduct(product);
                orderItem.setQuantity(quantity);
                orderItem.setPrice(price);
                orderItems.add(orderItem);
            }
            orderItemRepository.saveAll(orderItems);
            logger.info("Saved {} order items for order id: {}", orderItems.size(), orderId);

            return ResponseEntity.ok(Map.of("orderId", orderId));
        } catch (IllegalArgumentException e) {
            logger.error("Validation error during checkout: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            logger.error("State error during checkout: {}", e.getMessage());
            return ResponseEntity.status(500).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during checkout: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Lỗi khi thanh toán: " + e.getMessage());
        }
    }

    @Override
    public Order updatePaymentStatus(String orderId, String paymentStatus) {
        logger.info("Updating payment status for order {} to {}", orderId, paymentStatus);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        // Ngăn chặn việc cập nhật lại nếu trạng thái đã giống nhau (tránh cộng/trừ tồn kho nhiều lần)
        if (paymentStatus.equalsIgnoreCase(order.getPaymentStatus())) {
            return order;
        }

        order.setPaymentStatus(paymentStatus);

        if ("PAID".equalsIgnoreCase(paymentStatus)) {
            orderStatusRepository.findByStatusName("Paid")
                    .ifPresent(order::setOrderStatus);
        } else if ("FAILED".equalsIgnoreCase(paymentStatus)) {
            // Hoàn lại số lượng sản phẩm vào kho khi thanh toán thất bại
            List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
            for (OrderItem item : orderItems) {
                Product product = item.getProduct();
                if (product != null) {
                    product.setQuantity(product.getQuantity() + item.getQuantity());
                    productRepository.save(product);
                }
            }
            // Hoàn lại lượt sử dụng mã giảm giá
            if (order.getCoupon() != null) {
                Coupon coupon = order.getCoupon();
                coupon.setTimesUsed(coupon.getTimesUsed().subtract(BigDecimal.ONE));
                couponRepository.save(coupon);
            }
            orderStatusRepository.findByStatusName("Cancelled")
                    .ifPresent(order::setOrderStatus);
        }

        return orderRepository.save(order);
    }

    @Override
    public Order approveOrder(UUID orderId, UUID staffId) {
        String orderIdStr = orderIdAdapter.toString(orderId);
        logger.info("Approving order with id: {} by staffId: {}", orderIdStr, staffId);
        return approveOrderById(orderIdStr, staffId);
    }

    @Override
    public Order approveOrder(String orderId, UUID staffId) {
        logger.info("Approving order with id: {} by staffId: {}", orderId, staffId);
        return approveOrderById(orderId, staffId);
    }

    private Order approveOrderById(String orderId, UUID staffId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        StaffAccount staff = staffAccountRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));

        order.setOrderApprovedAt(new Date());
        order.setUpdatedBy(staff);

        OrderStatus approvedStatus = orderStatusRepository.findByStatusName("Approved")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái Approved"));
        order.setOrderStatus(approvedStatus);

        return orderRepository.save(order);
    }

    @Override
    public Order markOrderAsShipped(UUID orderId, UUID staffId) {
        String orderIdStr = orderIdAdapter.toString(orderId);
        logger.info("Marking order as shipped with id: {} by staffId: {}", orderIdStr, staffId);
        return markOrderAsShippedById(orderIdStr, staffId);
    }

    @Override
    public Order markOrderAsShipped(String orderId, UUID staffId) {
        logger.info("Marking order as shipped with id: {} by staffId: {}", orderId, staffId);
        return markOrderAsShippedById(orderId, staffId);
    }

    private Order markOrderAsShippedById(String orderId, UUID staffId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        StaffAccount staff = staffAccountRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));

        order.setOrderDeliveredCarrierDate(new Date());
        order.setUpdatedBy(staff);

        OrderStatus shippedStatus = orderStatusRepository.findByStatusName("Shipped")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái Shipped"));
        order.setOrderStatus(shippedStatus);

        return orderRepository.save(order);
    }

    @Override
    public Order customerAcceptOrder(UUID orderId) {
        String orderIdStr = orderIdAdapter.toString(orderId);
        logger.info("Customer accepting order with id: {}", orderIdStr);
        return customerAcceptOrderById(orderIdStr);
    }

    @Override
    public Order customerAcceptOrder(String orderId) {
        logger.info("Customer accepting order with id: {}", orderId);
        return customerAcceptOrderById(orderId);
    }

    private Order customerAcceptOrderById(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        order.setOrderDeliveredCustomerDate(new Date());

        OrderStatus completedStatus = orderStatusRepository.findByStatusName("Delivered")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái Delivered"));
        order.setOrderStatus(completedStatus);

        return orderRepository.save(order);
    }
}