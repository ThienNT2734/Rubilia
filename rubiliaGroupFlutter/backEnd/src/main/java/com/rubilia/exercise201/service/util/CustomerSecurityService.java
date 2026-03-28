package com.rubilia.exercise201.service.util;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.rubilia.exercise201.entity.Customer;
public interface CustomerSecurityService extends UserDetailsService{

    public Customer findByUserName(String user_name);
    
}
