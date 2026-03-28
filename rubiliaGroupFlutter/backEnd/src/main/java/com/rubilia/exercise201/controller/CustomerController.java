package com.rubilia.exercise201.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import com.rubilia.exercise201.entity.Customer;
import com.rubilia.exercise201.entity.CustomerAddress;
import com.rubilia.exercise201.entity.PasswordResetToken;
import com.rubilia.exercise201.repository.CustomerRepository;
import com.rubilia.exercise201.repository.PasswordResetTokenRepository;
import com.rubilia.exercise201.security.LoginRequest;
import com.rubilia.exercise201.service.CustomerAddressService;
import com.rubilia.exercise201.service.CustomerService;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    @Qualifier("customerAuthManager")
    private AuthenticationManager customerAuthManager;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerAddressService customerAddressService;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable UUID id) {
        return customerService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Customer> getCustomerByEmail(@PathVariable String email) {
        return customerService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerCustomer(@RequestBody RegisterRequest registerRequest) {
        try {
            if (customerService.existsByEmail(registerRequest.getUser_name())) {
                return ResponseEntity.badRequest().body("Email đã tồn tại.");
            }
            Customer customer = new Customer();
            customer.setUserName(registerRequest.getUser_name());
            customer.setEmail(registerRequest.getUser_name());
            customer.setPasswordHash(registerRequest.getPassword_hash());
            customer.setFirstName(registerRequest.getFirst_name() != null ? registerRequest.getFirst_name() : "Customer");
            customer.setLastName(registerRequest.getLast_name() != null ? registerRequest.getLast_name() : "Unknown");
            customer.setRegisteredAt(new Date());
            customer.setUpdatedAt(new Date());
            customer = customerService.save(customer);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Đăng ký thành công");
            response.put("id", customer.getId().toString());
            response.put("userName", customer.getUserName());
            response.put("email", customer.getEmail());
            response.put("firstName", customer.getFirstName());
            response.put("lastName", customer.getLastName());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi đăng ký: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginCustomer(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = customerAuthManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUser_name(), loginRequest.getPassword_hash())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            Customer customer = customerRepository.findByUserName(loginRequest.getUser_name());
            if (customer == null) {
                return ResponseEntity.badRequest().body("Không tìm thấy thông tin khách hàng.");
            }
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Đăng nhập khách hàng thành công");
            response.put("id", customer.getId().toString());
            response.put("userName", customer.getUserName());
            response.put("email", customer.getEmail());
            response.put("firstName", customer.getFirstName());
            response.put("lastName", customer.getLastName());
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body("Tên đăng nhập hoặc mật khẩu sai.");
        }
    }

    @GetMapping("/oauth2/success")
    public ResponseEntity<String> oauth2LoginSuccess(Authentication authentication) {
        if (authentication == null) {
            String errorResponse = "{\"status\": \"error\", \"error\": \"Không tìm thấy thông tin xác thực.\"}";
            return ResponseEntity.ok(
                "<html><body>" +
                "<script>" +
                "window.opener.postMessage(" + errorResponse + ", 'http://localhost:3000');" +
                "window.close();" +
                "</script>" +
                "</body></html>"
            );
        }

        if (!(authentication.getPrincipal() instanceof OidcUser oidcUser)) {
            String errorResponse = "{\"status\": \"error\", \"error\": \"Không tìm thấy thông tin người dùng Google.\"}";
            return ResponseEntity.ok(
                "<html><body>" +
                "<script>" +
                "window.opener.postMessage(" + errorResponse + ", 'http://localhost:3000');" +
                "window.close();" +
                "</script>" +
                "</body></html>"
            );
        }

        String email = oidcUser.getEmail();
        String firstName = oidcUser.getGivenName();
        String lastName = oidcUser.getFamilyName();

        if (email == null) {
            String errorResponse = "{\"status\": \"error\", \"error\": \"Không thể lấy email từ Google.\"}";
            return ResponseEntity.ok(
                "<html><body>" +
                "<script>" +
                "window.opener.postMessage(" + errorResponse + ", 'http://localhost:3000');" +
                "window.close();" +
                "</script>" +
                "</body></html>"
            );
        }

        Optional<Customer> existingCustomer = customerService.findByEmail(email);
        Customer customer;

        if (existingCustomer.isPresent()) {
            customer = existingCustomer.get();
            String successResponse = String.format(
                "{\"status\": \"success\", \"data\": {\"id\": \"%s\", \"userName\": \"%s\", \"email\": \"%s\", \"firstName\": \"%s\", \"lastName\": \"%s\", \"isOAuth2\": %b}}",
                customer.getId().toString(),
                customer.getUserName(),
                customer.getEmail(),
                customer.getFirstName(),
                customer.getLastName(),
                passwordEncoder.matches("oauth2_user", customer.getPasswordHash())
            );
            return ResponseEntity.ok(
                "<html><body>" +
                "<script>" +
                "window.opener.postMessage(" + successResponse + ", 'http://localhost:3000');" +
                "window.close();" +
                "</script>" +
                "</body></html>"
            );
        } else {
            if (firstName == null || lastName == null) {
                String infoResponse = String.format(
                    "{\"status\": \"info_required\", \"data\": {\"provider\": \"google\", \"email\": \"%s\", \"firstName\": %s, \"lastName\": %s}}",
                    email,
                    firstName != null ? "\"" + firstName + "\"" : "null",
                    lastName != null ? "\"" + lastName + "\"" : "null"
                );
                return ResponseEntity.ok(
                    "<html><body>" +
                    "<script>" +
                    "window.opener.postMessage(" + infoResponse + ", 'http://localhost:3000');" +
                    "window.close();" +
                    "</script>" +
                    "</body></html>"
                );
            }

            customer = new Customer();
            customer.setEmail(email);
            customer.setUserName(email);
            customer.setPasswordHash("oauth2_user");
            customer.setFirstName(firstName != null ? firstName : "User");
            customer.setLastName(lastName != null ? lastName : "Google");
            customer.setRegisteredAt(new Date());
            customer.setUpdatedAt(new Date());
            customer.setActive(true);
            customer = customerService.save(customer);

            String successResponse = String.format(
                "{\"status\": \"success\", \"data\": {\"id\": \"%s\", \"userName\": \"%s\", \"email\": \"%s\", \"firstName\": \"%s\", \"lastName\": \"%s\", \"isOAuth2\": %b}}",
                customer.getId().toString(),
                customer.getUserName(),
                customer.getEmail(),
                customer.getFirstName(),
                customer.getLastName(),
                true
            );
            return ResponseEntity.ok(
                "<html><body>" +
                "<script>" +
                "window.opener.postMessage(" + successResponse + ", 'http://localhost:3000');" +
                "window.close();" +
                "</script>" +
                "</body></html>"
            );
        }
    }

    @GetMapping("/oauth2/failure")
    public ResponseEntity<String> oauth2LoginFailure() {
        String errorResponse = "{\"status\": \"error\", \"error\": \"Đăng nhập OAuth2 thất bại.\"}";
        return ResponseEntity.ok(
            "<html><body>" +
            "<script>" +
            "window.opener.postMessage(" + errorResponse + ", 'http://localhost:3000');" +
            "window.close();" +
            "</script>" +
            "</body></html>"
        );
    }

    @PostMapping("/oauth2/register")
    public ResponseEntity<?> oauth2Register(@RequestBody OAuth2RegisterRequest registerRequest) {
        try {
            if (customerService.existsByEmail(registerRequest.getEmail())) {
                return ResponseEntity.badRequest().body("Email đã tồn tại.");
            }
            Customer customer = new Customer();
            customer.setEmail(registerRequest.getEmail());
            customer.setUserName(registerRequest.getEmail());
            customer.setPasswordHash("oauth2_user");
            customer.setFirstName(registerRequest.getFirstName());
            customer.setLastName(registerRequest.getLastName());
            customer.setRegisteredAt(new Date());
            customer.setUpdatedAt(new Date());
            customer.setActive(true);
            customer = customerService.save(customer);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Đăng ký OAuth2 thành công");
            response.put("id", customer.getId().toString());
            response.put("userName", customer.getUserName());
            response.put("email", customer.getEmail());
            response.put("firstName", customer.getFirstName());
            response.put("lastName", customer.getLastName());
            response.put("isOAuth2", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi đăng ký OAuth2: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/is-oauth2")
    public ResponseEntity<Map<String, Boolean>> isOAuth2Account(@PathVariable UUID id) {
        try {
            Customer customer = customerRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng."));
            boolean isOAuth2 = passwordEncoder.matches("oauth2_user", customer.getPasswordHash());
            Map<String, Boolean> response = new HashMap<>();
            response.put("isOAuth2", isOAuth2);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("isOAuth2", false));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        try {
            String email = forgotPasswordRequest.getEmail();
            Optional<Customer> customerOpt = customerService.findByEmail(email);
            if (!customerOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Email không tồn tại.");
            }

            Customer customer = customerOpt.get();
            boolean isOAuth2 = passwordEncoder.matches("oauth2_user", customer.getPasswordHash());
            if (isOAuth2) {
                return ResponseEntity.badRequest().body("Tài khoản này sử dụng đăng nhập bằng Google. Không thể đặt lại mật khẩu.");
            }

            // Tạo token đặt lại mật khẩu
            String token = UUID.randomUUID().toString();
            LocalDateTime createdAt = LocalDateTime.now();
            LocalDateTime expiresAt = createdAt.plus(1, ChronoUnit.HOURS);

            // Lưu token vào bảng password_reset_tokens
            PasswordResetToken resetToken = new PasswordResetToken(email, token, createdAt, expiresAt);
            passwordResetTokenRepository.save(resetToken);

            // Gửi email với đường dẫn đặt lại mật khẩu
            String resetLink = "http://localhost:3000/reset-password?token=" + token;
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Đặt lại mật khẩu - Rubilia");
            message.setText("Nhấn vào đường dẫn sau để đặt lại mật khẩu của bạn: " + resetLink + "\nĐường dẫn này sẽ hết hạn sau 1 giờ.");
            mailSender.send(message);

            return ResponseEntity.ok("Email đặt lại mật khẩu đã được gửi.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi gửi email đặt lại mật khẩu: " + e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        try {
            String token = resetPasswordRequest.getToken();
            String newPassword = resetPasswordRequest.getNewPassword();
            String confirmPassword = resetPasswordRequest.getConfirmPassword();

            // Kiểm tra token
            Optional<PasswordResetToken> tokenOpt = passwordResetTokenRepository.findByToken(token);
            if (!tokenOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Token không hợp lệ.");
            }

            PasswordResetToken resetToken = tokenOpt.get();
            if (LocalDateTime.now().isAfter(resetToken.getExpiresAt())) {
                return ResponseEntity.badRequest().body("Token đã hết hạn.");
            }

            // Kiểm tra email
            Optional<Customer> customerOpt = customerService.findByEmail(resetToken.getEmail());
            if (!customerOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Email không tồn tại.");
            }

            Customer customer = customerOpt.get();
            boolean isOAuth2 = passwordEncoder.matches("oauth2_user", customer.getPasswordHash());
            if (isOAuth2) {
                return ResponseEntity.badRequest().body("Tài khoản này sử dụng đăng nhập bằng Google. Không thể đặt lại mật khẩu.");
            }

            // Kiểm tra mật khẩu mới và xác nhận khớp nhau
            if (!newPassword.equals(confirmPassword)) {
                return ResponseEntity.badRequest().body("Mật khẩu mới và xác nhận không khớp.");
            }

            // Cập nhật mật khẩu mới
            String encodedPassword = passwordEncoder.encode(newPassword);
            customer.setPasswordHash(encodedPassword);
            customer.setUpdatedAt(new Date());
            customerService.updateWithoutPasswordHash(customer.getId(), customer);

            // Xóa token sau khi sử dụng
            passwordResetTokenRepository.delete(resetToken);

            return ResponseEntity.ok("Đặt lại mật khẩu thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi đặt lại mật khẩu: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<?> changePassword(@PathVariable UUID id, @RequestBody ChangePasswordRequest passwordRequest) {
        try {
            Customer customer = customerRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng."));
            boolean isOAuth2 = passwordEncoder.matches("oauth2_user", customer.getPasswordHash());

            if (!isOAuth2) {
                Authentication authentication = customerAuthManager.authenticate(
                    new UsernamePasswordAuthenticationToken(customer.getUserName(), passwordRequest.getCurrentPassword())
                );
                if (authentication == null || !authentication.isAuthenticated()) {
                    return ResponseEntity.badRequest().body("Mật khẩu hiện tại không đúng.");
                }
            }

            if (!passwordRequest.getNewPassword().equals(passwordRequest.getConfirmPassword())) {
                return ResponseEntity.badRequest().body("Mật khẩu mới và xác nhận không khớp.");
            }

            String encodedPassword = passwordEncoder.encode(passwordRequest.getNewPassword());
            customer.setPasswordHash(encodedPassword);
            customer.setUpdatedAt(new Date());
            customerService.updateWithoutPasswordHash(id, customer);

            return ResponseEntity.ok("Đổi mật khẩu thành công!");
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body("Mật khẩu hiện tại không đúng.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi đổi mật khẩu: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createCustomer(@RequestBody Customer customer) {
        if (customerService.existsByEmail(customer.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }
        return ResponseEntity.ok(customerService.save(customer));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable UUID id, @RequestBody Customer customer) {
        if (!customerService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        if (!customer.getEmail().equals(customerService.findById(id).get().getEmail()) &&
                customerService.existsByEmail(customer.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }
        try {
            customer.setId(id);
            Customer updated = customerService.update(id, customer);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID id) {
        if (!customerService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        customerService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/addresses")
    public ResponseEntity<List<CustomerAddress>> getCustomerAddresses(@PathVariable UUID id) {
        if (!customerService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(customerAddressService.findByCustomer(customerService.findById(id).get()));
    }

    @PostMapping("/{id}/addresses")
    public ResponseEntity<CustomerAddress> addCustomerAddress(
            @PathVariable UUID id,
            @RequestBody CustomerAddress address) {
        return customerService.findById(id)
                .map(customer -> {
                    address.setCustomer(customer);
                    return ResponseEntity.ok(customerAddressService.save(address));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}

class RegisterRequest {
    private String user_name;
    private String password_hash;
    private String first_name;
    private String last_name;

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPassword_hash() {
        return password_hash;
    }

    public void setPassword_hash(String password_hash) {
        this.password_hash = password_hash;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }
}

class ChangePasswordRequest {
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}

class OAuth2RegisterRequest {
    private String email;
    private String firstName;
    private String lastName;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}

class ForgotPasswordRequest {
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

class ResetPasswordRequest {
    private String token;
    private String newPassword;
    private String confirmPassword;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}