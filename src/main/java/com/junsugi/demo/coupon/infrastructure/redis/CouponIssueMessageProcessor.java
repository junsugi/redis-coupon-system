package com.junsugi.demo.coupon.infrastructure.redis;

import com.junsugi.demo.coupon.application.command.CouponIssuePersistCommand;
import com.junsugi.demo.coupon.application.service.CouponIssuePersistService;
import com.junsugi.demo.coupon.infrastructure.redis.stream.message.CouponIssueStreamMessage;
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
        CouponIssueStreamMessage message = CouponIssueStreamMessage.from(values);

        Long couponId = message.couponId();
        Long userId = message.userId();
        LocalDateTime issuedAt = message.issuedAt();

        couponIssuePersistService.persist(
                new CouponIssuePersistCommand(couponId, userId, issuedAt)
        );

        log.info("Coupon issue event persisted. couponId={}, userId={}", couponId, userId);
    }
}
