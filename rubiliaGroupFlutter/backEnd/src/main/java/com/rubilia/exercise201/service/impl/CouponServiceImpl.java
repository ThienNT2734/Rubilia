package com.rubilia.exercise201.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rubilia.exercise201.entity.Coupon;
import com.rubilia.exercise201.repository.CouponRepository;
import com.rubilia.exercise201.service.CouponService;

@Service
@Transactional
public class CouponServiceImpl implements CouponService {

    @Autowired
    private CouponRepository couponRepository;

    @Override
    public List<Coupon> findAll() {
        return couponRepository.findAll();
    }

    @Override
    public Optional<Coupon> findById(UUID id) {
        return couponRepository.findById(id);
    }

    @Override
    public Optional<Coupon> findByCode(String code) {
        return couponRepository.findByCode(code);
    }

    @Override
    public Coupon save(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    @Override
    public void deleteById(UUID id) {
        couponRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return couponRepository.existsById(id);
    }

    @Override
    public boolean existsByCode(String code) {
        return couponRepository.existsByCode(code);
    }

    @Override
    public List<Coupon> findByMaxUsageGreaterThanTimesUsed() {
        return couponRepository.findByMaxUsageGreaterThanTimesUsed();
    }

    @Override
    public List<Coupon> findValidCoupons(Date date) {
        return couponRepository.findValidCoupons(date);
    }

    @Override
    public void incrementTimesUsed(Coupon coupon) {
        coupon.setTimesUsed(coupon.getTimesUsed().add(new java.math.BigDecimal(1)));
        couponRepository.save(coupon);
    }

    @Override
    public boolean isValidCoupon(Coupon coupon) {
        Date now = new Date();
        return (coupon.getCouponStartDate() == null || now.after(coupon.getCouponStartDate())) &&
               (coupon.getCouponEndDate() == null || now.before(coupon.getCouponEndDate())) &&
               (coupon.getMaxUsage() == null || coupon.getMaxUsage().compareTo(coupon.getTimesUsed()) > 0);
    }
}