package com.junsugi.demo.coupon.infrastructure.redis.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "coupon.issue.stream")
public record CouponIssueProperties(
        Pending pending,
        Dlq dlq
) {
    public record Pending(
            Duration scheduleDelay,
            Duration minIdleTime,
            int claimCount,
            int maxDeliveryCount
    ) {
    }

    public record Dlq(
            String key,
            String publishedSetKey
    ) {
    }
}
