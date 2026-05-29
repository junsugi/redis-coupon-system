package com.junsugi.demo.coupon.presentation.response;

import com.junsugi.demo.coupon.domain.Coupon;
import com.junsugi.demo.coupon.domain.CouponStatus;
import com.junsugi.demo.coupon.domain.DiscountType;

import java.time.LocalDateTime;

public record CouponCreateResponse (
        Long couponId,
        String name,
        int totalQuantity,
        DiscountType discountType,
        int discountValue,
        LocalDateTime issueStartAt,
        LocalDateTime issueEndAt,
        CouponStatus status
) {
    public static CouponCreateResponse from(Coupon coupon) {
        return new CouponCreateResponse(
                coupon.getId(),
                coupon.getName(),
                coupon.getTotalQuantity(),
                coupon.getDiscountType(),
                coupon.getDiscountValue(),
                coupon.getIssueStartAt(),
                coupon.getIssueEndAt(),
                coupon.getStatus()
        );
    }
}
