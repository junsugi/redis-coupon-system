package com.junsugi.demo.coupon.infrastructure.repository;

import com.junsugi.demo.coupon.domain.CouponIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponIssueRepository extends JpaRepository<CouponIssue, Long> {
}
