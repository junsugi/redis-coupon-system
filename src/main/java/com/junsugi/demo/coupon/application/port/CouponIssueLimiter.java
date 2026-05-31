package com.junsugi.demo.coupon.application.port;

import java.time.LocalDateTime;

public interface CouponIssueLimiter {
    void issue(Long couponId, Long userId, int totalQuantity, LocalDateTime issuedAt);
}
