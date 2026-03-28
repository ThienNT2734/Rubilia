package com.rubilia.exercise201.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import com.rubilia.exercise201.entity.Role;
import com.rubilia.exercise201.entity.StaffAccount;
import com.rubilia.exercise201.repository.RoleRepository;
import com.rubilia.exercise201.repository.StaffAccountRepository;
import com.rubilia.exercise201.security.LoginRequest;
import com.rubilia.exercise201.service.StaffAccountService;
import com.rubilia.exercise201.service.util.StaffAccountFinder;

@RestController
@RequestMapping("/api/staff")
public class StaffAccountController {
    @Autowired
    @Qualifier("staffAuthManager")
    private AuthenticationManager staffAuthManager;

    @Autowired
    private StaffAccountService staffAccountService;

    @Autowired
    private StaffAccountRepository staffAccountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private StaffAccountFinder staffAccountFinder;

    @GetMapping
    public ResponseEntity<List<StaffAccount>> getAllStaffAccounts() {
        return ResponseEntity.ok(staffAccountService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StaffAccount> getStaffAccountById(@PathVariable UUID id) {
        return staffAccountService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<StaffAccount> getStaffAccountByEmail(@PathVariable String email) {
        return staffAccountService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerStaffAccount(@RequestBody StaffAccount staffAccount) {
        try {
            if (staffAccountFinder.findByUserName(staffAccount.getUser_name()).isPresent()) {
                return ResponseEntity.badRequest().body("Tên đăng nhập đã tồn tại.");
            }
            if (staffAccountService.findByEmail(staffAccount.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().body("Email đã được sử dụng.");
            }

            Role role = roleRepository.findByRoleName("ADMIN")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò ADMIN trong cơ sở dữ liệu. Vui lòng tạo vai trò ADMIN trước khi đăng ký."));
            staffAccount.setRole(role);

            if (staffAccount.getCreated_at() == null) {
                staffAccount.setCreated_at(new Date());
            }
            if (staffAccount.getUpdated_at() == null) {
                staffAccount.setUpdated_at(new Date());
            }

            StaffAccount savedStaff = staffAccountService.save(staffAccount);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Đăng ký thành công!");
            response.put("user_name", savedStaff.getUser_name());
            response.put("email", savedStaff.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi khi đăng ký: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<StaffAccount> createStaffAccount(@RequestBody StaffAccount staffAccount) {
        return ResponseEntity.ok(staffAccountService.save(staffAccount));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StaffAccount> updateStaffAccount(
            @PathVariable UUID id,
            @RequestBody StaffAccount staffAccount) {
        if (!staffAccountService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        staffAccount.setId(id);
        return ResponseEntity.ok(staffAccountService.save(staffAccount));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStaffAccount(@PathVariable UUID id) {
        if (!staffAccountService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        staffAccountService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication auth = staffAuthManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUser_name(), request.getPassword_hash())
            );

            Optional<StaffAccount> staffOptional = staffAccountFinder.findByUserName(request.getUser_name());
            if (!staffOptional.isPresent()) {
                return ResponseEntity.badRequest().body("Không tìm thấy thông tin nhân viên.");
            }

            StaffAccount staff = staffOptional.get();

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Đăng nhập nhân viên thành công");
            response.put("user_name", staff.getUser_name());
            response.put("role", staff.getRole().getRole_name());
            response.put("id", staff.getId());

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body("Tên đăng nhập hoặc mật khẩu sai (nhân viên)");
        }
    }
}