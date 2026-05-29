package com.junsugi.demo.coupon.application.command;


import com.junsugi.demo.coupon.domain.DiscountType;

import java.time.LocalDateTime;

public record CouponCreateCommand (
        String name,
        int totalQuantity,
        DiscountType discountType,
        int discountValue,
        LocalDateTime issueStartAt,
        LocalDateTime issueEndAt
){
}