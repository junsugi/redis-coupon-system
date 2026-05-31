package com.junsugi.demo.coupon.application.port;

import com.junsugi.demo.coupon.domain.CouponIssue;

public interface CouponIssueRepository {
    CouponIssue save(CouponIssue couponIssue);
    void saveIfAbsent(CouponIssue couponIssue);
}
