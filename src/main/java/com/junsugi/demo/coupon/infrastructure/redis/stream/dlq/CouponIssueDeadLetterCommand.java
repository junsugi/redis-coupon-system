package com.junsugi.demo.coupon.infrastructure.redis.stream.dlq;

import java.time.LocalDateTime;

public record CouponIssueDeadLetterCommand (
        String originalRecordId,
        Long couponId,
        Long userId,
        LocalDateTime issuedAt,
        String errorMessage,
        LocalDateTime failedAt,
        long deliveryCount
){
}
