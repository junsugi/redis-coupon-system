package com.junsugi.demo.coupon.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class CouponIssueStreamListener implements StreamListener<String, MapRecord<String, String, String>> {

    private final CouponIssueProcessor couponIssueProcessor;
    private final StringRedisTemplate redisTemplate;


    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        try {
            couponIssueProcessor.Process(message.getValue());

            redisTemplate.opsForStream().acknowledge(
                    CouponIssueStreamConstants.ISSUE_STREAM_KEY,
                    CouponIssueStreamConstants.ISSUE_GROUP,
                    message.getId()
            );


        } catch (Exception e) {
            log.error("Failed to process coupon issue event. messageId={}, error={}",
                    message.getId(), e.getMessage(), e);

            // 중요: 실패하면 ACK 하지 않음.
            // 그래야 Pending 상태로 남고 나중에 재처리 가능.
        }
    }
}
