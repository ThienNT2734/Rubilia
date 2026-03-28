package com.rubilia.exercise201.service.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.rubilia.exercise201.entity.Customer;
import com.rubilia.exercise201.repository.CustomerRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerSecurityServiceImpl implements CustomerSecurityService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public Customer findByUserName(String userName) {
        return customerRepository.findByUserName(userName);
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Customer customer = findByUserName(userName);
        if (customer == null) {
            throw new UsernameNotFoundException("Tài khoản không tồn tại!");
        }
        if (customer.getPasswordHash() == null) {
            throw new UsernameNotFoundException("Mật khẩu không hợp lệ!");
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        return new org.springframework.security.core.userdetails.User(
            customer.getUserName(),
            customer.getPasswordHash(),
            authorities
        );
    }
}