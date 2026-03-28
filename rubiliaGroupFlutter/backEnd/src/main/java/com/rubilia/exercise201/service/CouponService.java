package com.rubilia.exercise201.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.rubilia.exercise201.entity.Coupon;

public interface CouponService {
    List<Coupon> findAll();
    Optional<Coupon> findById(UUID id);
    Optional<Coupon> findByCode(String code);
    Coupon save(Coupon coupon);
    void deleteById(UUID id);
    boolean existsById(UUID id);
    boolean existsByCode(String code);
    List<Coupon> findByMaxUsageGreaterThanTimesUsed();
    List<Coupon> findValidCoupons(Date date);
    void incrementTimesUsed(Coupon coupon);
    boolean isValidCoupon(Coupon coupon);
}