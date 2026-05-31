package com.junsugi.demo.coupon.application.command;

import java.time.LocalDateTime;

public record CouponIssuePersistCommand (
        Long couponId,
        Long userId,
        LocalDateTime issuedAt
){

}
