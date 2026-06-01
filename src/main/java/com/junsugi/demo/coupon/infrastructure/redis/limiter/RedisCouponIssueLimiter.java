package com.junsugi.demo.coupon.infrastructure.redis.limiter;

import com.junsugi.demo.coupon.application.port.CouponIssueLimiter;
import com.junsugi.demo.coupon.infrastructure.redis.CouponIssueStreamConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RedisCouponIssueLimiter implements CouponIssueLimiter {

    private static final Long SUCCESS = 0L;
    private static final Long DUPLICATE_ISSUE = 1L;
    private static final Long SOLD_OUT = 2L;

    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> couponIssueScript;

    @Override
    public void issue(Long couponId, Long userId, int totalQuantity, LocalDateTime issuedAt) {
        Long result = redisTemplate.execute(
                couponIssueScript,
                List.of(
                        getIssuedCountKey(couponId),
                        getUserIssuedKey(couponId, userId),
                        CouponIssueStreamConstants.ISSUE_STREAM_KEY
                ),
                String.valueOf(totalQuantity),
                String.valueOf(Duration.ofDays(7).toSeconds()),
                String.valueOf(couponId),
                String.valueOf(userId),
                issuedAt.toString()
        );

        validateResult(result);
    }

    private void validateResult(Long result) {
        if (SUCCESS.equals(result)) return;

        if (DUPLICATE_ISSUE.equals(result))
            throw new IllegalArgumentException("이미 발급받은 쿠폰입니다.");

        if (SOLD_OUT.equals(result))
            throw new IllegalArgumentException("쿠폰이 모두 소진되었습니다.");

        throw new IllegalArgumentException("쿠폰 발급 처리 중 알 수 없는 오류가 발생했습니다.");
    }

    private String getIssuedCountKey(Long couponId) {
        return String.format("coupon:{%d}:issued-count", couponId);
    }

    private String getUserIssuedKey(Long couponId, Long userId) {
        return String.format("coupon:{%d}:user:%d", couponId, userId);
    }
}
