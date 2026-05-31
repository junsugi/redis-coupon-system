package com.junsugi.demo.coupon.infrastructure.persistence;

import com.junsugi.demo.coupon.domain.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
}
