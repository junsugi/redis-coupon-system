package com.junsugi.demo.coupon.presentation.mapper;

import com.junsugi.demo.coupon.domain.Coupon;
import com.junsugi.demo.coupon.presentation.response.CouponResponse;

public class CouponMapper {
    private CouponMapper() {}

    public static CouponResponse toResponse(Coupon coupon) {
        return new CouponResponse(
                coupon.getId(),
                coupon.getName(),
                coupon.getTotalQuantity(),
                coupon.getIssuedQuantity(),
                coupon.getDiscountType(),
                coupon.getDiscountValue(),
                coupon.getIssueStartAt(),
                coupon.getIssueEndAt(),
                coupon.getStatus(),
                coupon.getCreatedAt(),
                coupon.getUpdatedAt()
        );
    }
}
