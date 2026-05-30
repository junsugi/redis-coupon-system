package com.junsugi.demo.coupon.application.port;

public interface CouponIssueLimiter {
    void issue(Long couponId, Long userId, int totalQuantity);
}
