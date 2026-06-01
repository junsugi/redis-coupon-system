package com.junsugi.demo.coupon.infrastructure.redis;

import com.junsugi.demo.coupon.application.command.CouponIssuePersistCommand;
import com.junsugi.demo.coupon.application.service.CouponIssuePersistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponIssueMessageProcessor {

    private final CouponIssuePersistService couponIssuePersistService;

    public void process(Map<String, String> values){
        Long couponId = getRequiredLong(values, "couponId");
        Long userId = getRequiredLong(values, "userId");
        LocalDateTime issuedAt = getRequiredLocalDateTime(values);

        couponIssuePersistService.persist(
                new CouponIssuePersistCommand(couponId, userId, issuedAt)
        );

        log.info("Coupon issue event persisted. couponId={}, userId={}", couponId, userId);
    }

    private Long getRequiredLong(Map<String, String> values, String key) {
        String value = values.get(key);

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Stream message field is missing. key=" + key);
        }

        return Long.parseLong(value);
    }

    private LocalDateTime getRequiredLocalDateTime(Map<String, String> values) {
        String value = values.get("issuedAt");

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Stream message field is missing. key=" + "issuedAt");
        }

        return LocalDateTime.parse(value);
    }
}
