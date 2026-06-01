package com.junsugi.demo.coupon.infrastructure.persistence;

import com.junsugi.demo.coupon.domain.CouponIssueFailureLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponIssueFailureLogRepository
        extends JpaRepository<CouponIssueFailureLog, Long> {

    boolean existsByOriginalRecordId(String originalRecordId);
}