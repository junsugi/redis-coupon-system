package com.junsugi.demo.coupon.infrastructure.redis.stream.dlq;

import com.junsugi.demo.coupon.infrastructure.redis.config.CouponIssueProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CouponIssueDeadLetterPublisher {

    private static final int ERROR_MESSAGE_MAX_LENGTH = 1000;

    private final StringRedisTemplate redisTemplate;
    private final CouponIssueProperties properties;
    private final DefaultRedisScript<Long> couponIssueDeadLetterScript;

    public void publish(CouponIssueDeadLetterCommand command) {
        Long result = redisTemplate.execute(
                couponIssueDeadLetterScript,
                List.of(
                        properties.dlq().key(),
                        properties.dlq().publishedSetKey()
                ),
                command.originalRecordId(),
                String.valueOf(command.couponId()),
                String.valueOf(command.userId()),
                command.issuedAt().toString(),
                limitErrorMessageLength(command.errorMessage()),
                command.failedAt().toString(),
                String.valueOf(command.deliveryCount())
        );

        if (result == null)
            throw new IllegalArgumentException("Failed to publish coupon issue dead letter");
    }

    private String limitErrorMessageLength(String value){
        if (value == null) return "";
        if (value.length() <= ERROR_MESSAGE_MAX_LENGTH) return value;

        return value.substring(0, ERROR_MESSAGE_MAX_LENGTH);
    }
}
