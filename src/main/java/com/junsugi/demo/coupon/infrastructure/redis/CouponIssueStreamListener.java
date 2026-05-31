package com.junsugi.demo.coupon.infrastructure.redis;

import com.junsugi.demo.coupon.application.command.CouponIssuePersistCommand;
import com.junsugi.demo.coupon.application.service.CouponIssuePersistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponIssueStreamListener implements StreamListener<String, MapRecord<String, String, String>> {

    private final CouponIssuePersistService couponIssuePersistService;
    private final StringRedisTemplate redisTemplate;


    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        try {
            Map<String, String> values = message.getValue();

            Long couponId = Long.parseLong(values.get("couponId"));
            Long userId = Long.parseLong(values.get("userId"));
            LocalDateTime issuedAt = LocalDateTime.parse(values.get("issuedAt"));

            couponIssuePersistService.persist(
                    new CouponIssuePersistCommand(couponId, userId, issuedAt)
            );

            redisTemplate.opsForStream().acknowledge(
                    CouponIssueStreamConstants.ISSUE_STREAM_KEY,
                    CouponIssueStreamConstants.ISSUE_GROUP,
                    message.getId()
            );

            log.info("Coupon issue event persisted. messageId={}, couponId={}, userId={}",
                    message.getId(), couponId, userId);
        } catch (Exception e) {
            log.error("Failed to process coupon issue event. messageId={}, error={}",
                    message.getId(), e.getMessage(), e);

            // 중요: 실패하면 ACK 하지 않음.
            // 그래야 Pending 상태로 남고 나중에 재처리 가능.
        }
    }
}
