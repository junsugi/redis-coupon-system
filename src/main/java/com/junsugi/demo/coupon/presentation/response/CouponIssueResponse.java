package com.junsugi.demo.coupon.presentation.response;

import java.time.LocalDateTime;

public record CouponIssueResponse (
        Long couponId,
        Long userId,
        LocalDateTime issuedAt,
        String status
){
    public static CouponIssueResponse create(Long couponId, Long userId, LocalDateTime issuedAt, String status) {
        return new CouponIssueResponse(
                couponId,
                userId,
                issuedAt,
                status
        );
    }
}
