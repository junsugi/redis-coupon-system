package com.junsugi.demo.coupon.presentation.response;

import com.junsugi.demo.coupon.domain.CouponIssue;

import java.time.LocalDateTime;

public record CouponIssueResponse (
        Long issueId,
        Long couponId,
        Long userId,
        LocalDateTime createdAt
){
    public static CouponIssueResponse from(CouponIssue issuedCoupon) {
        return new CouponIssueResponse(
                issuedCoupon.getId(),
                issuedCoupon.getCouponId(),
                issuedCoupon.getUserId(),
                issuedCoupon.getCreateAt()
        );
    }
}
