package com.junsugi.demo.coupon.infrastructure.persistence;

import com.junsugi.demo.coupon.domain.CouponIssue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponIssueJpaRepository extends JpaRepository<CouponIssue, Long> {
}
