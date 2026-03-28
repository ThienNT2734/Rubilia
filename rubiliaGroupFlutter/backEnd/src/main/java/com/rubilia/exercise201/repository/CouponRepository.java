package com.rubilia.exercise201.repository;

import com.rubilia.exercise201.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, UUID> {
    Optional<Coupon> findByCode(String code);
    boolean existsByCode(String code);
    @Query("SELECT c FROM Coupon c WHERE c.maxUsage > c.timesUsed")
    List<Coupon> findByMaxUsageGreaterThanTimesUsed();
    @Query("SELECT c FROM Coupon c WHERE (c.couponStartDate IS NULL OR c.couponStartDate <= :date) AND (c.couponEndDate IS NULL OR c.couponEndDate >= :date)")
    List<Coupon> findValidCoupons(@Param("date") Date date);
}