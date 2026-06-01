package com.junsugi.demo.coupon.infrastructure.redis.stream.message;

import java.time.LocalDateTime;
import java.util.Map;

public record CouponIssueStreamMessage(
        Long couponId,
        Long userId,
        LocalDateTime issuedAt
) {
    public static CouponIssueStreamMessage from(Map<String, String> values) {
        return new CouponIssueStreamMessage(
                getRequiredLong(values, "couponId"),
                getRequiredLong(values, "userId"),
                getRequiredLocalDateTime(values, "issuedAt")
        );
    }

    private static Long getRequiredLong(Map<String, String> values, String key) {
        String value = values.get(key);

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Stream message field is missing. key=" + key);
        }

        return Long.parseLong(value);
    }

    private static LocalDateTime getRequiredLocalDateTime(Map<String, String> values, String key) {
        String value = values.get(key);

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Stream message field is missing. key=" + key);
        }

        return LocalDateTime.parse(value);
    }
}
