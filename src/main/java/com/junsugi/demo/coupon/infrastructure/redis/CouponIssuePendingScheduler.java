package com.junsugi.demo.coupon.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "coupon.issue.pending-worker.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class CouponIssuePendingScheduler {

    private final CouponIssuePendingWorker pendingWorker;

    @Scheduled(fixedDelay = 5000)
    public void recoverPendingMessage() {
        pendingWorker.recoverPendingMessage();
    }
}
