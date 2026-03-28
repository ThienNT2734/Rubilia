package com.rubilia.exercise201.service.util;

import com.rubilia.exercise201.entity.StaffAccount;
import com.rubilia.exercise201.repository.StaffAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class StaffAccountFinder {

    @Autowired
    private StaffAccountRepository staffAccountRepository;

    public Optional<StaffAccount> findByUserName(String userName) {
        // Tạm thời giả lập logic tìm kiếm
        // Trong thực tế, cần truy vấn cơ sở dữ liệu bằng cách mở rộng StaffAccountRepository
        return staffAccountRepository.findAll().stream()
                .filter(staff -> userName.equals(staff.getUser_name()))
                .findFirst();
    }
}