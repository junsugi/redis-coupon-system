package com.junsugi.demo.coupon.application.command;

import java.time.LocalDateTime;

public record CouponIssueFailureLogCommand(
        String originalRecordId,
        Long couponId,
        Long userId,
        LocalDateTime issuedAt,
        String errorMessage,
        LocalDateTime failedAt,
        long deliveryCount
) {
}