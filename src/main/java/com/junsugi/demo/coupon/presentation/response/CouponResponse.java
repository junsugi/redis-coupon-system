package com.junsugi.demo.coupon.presentation.response;

import com.junsugi.demo.coupon.domain.CouponStatus;
import com.junsugi.demo.coupon.domain.DiscountType;

import java.time.LocalDateTime;

public record CouponResponse(
        Long couponId,
        String name,
        int totalQuantity,
        int issuedQuantity,
        DiscountType discountType,
        int discountValue,
        LocalDateTime issueStartAt,
        LocalDateTime issueEndAt,
        CouponStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
