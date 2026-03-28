package com.rubilia.exercise201.service.util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.rubilia.exercise201.entity.Customer;
import com.rubilia.exercise201.entity.Role;
import com.rubilia.exercise201.entity.StaffAccount;
import com.rubilia.exercise201.repository.CustomerRepository;
import com.rubilia.exercise201.repository.RoleRepository;
import com.rubilia.exercise201.repository.StaffAccountRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StaffAccountSecurityServiceImpl implements StaffAccountSecurityService {
    @Autowired
    private StaffAccountRepository staffAccountRepository;

    @Override
    public StaffAccount findByUserName(String user_name) {
        return staffAccountRepository.findByUser_name(user_name);
    }

    @Override
    public UserDetails loadUserByUsername(String user_name) throws UsernameNotFoundException {
        StaffAccount staffAccount = findByUserName(user_name);
        if (staffAccount == null) {
            throw new UsernameNotFoundException("Tài khoản không tồn tại!");
        }
    
        // Lấy role từ tài khoản và tạo authorities cho user
        Role role = staffAccount.getRole();
        List<GrantedAuthority> authorities = role != null ? 
            List.of(new SimpleGrantedAuthority("ROLE_" + role.getRole_name())) : 
            new ArrayList<>();
    
        return new org.springframework.security.core.userdetails.User(
            staffAccount.getUser_name(),
            staffAccount.getPassword_hash(),
            authorities
        );
    }
}
