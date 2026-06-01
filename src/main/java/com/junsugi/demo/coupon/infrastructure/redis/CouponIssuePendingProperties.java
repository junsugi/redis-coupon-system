package com.junsugi.demo.coupon.infrastructure.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "coupon.issue.stream.pending")
public record CouponIssuePendingProperties (
    Duration minIdleTime,
    int claimCount
){

}
