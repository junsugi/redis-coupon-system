package com.junsugi.demo.coupon.infrastructure.redis.stream.pending;

import com.junsugi.demo.coupon.infrastructure.redis.CouponIssueMessageProcessor;
import com.junsugi.demo.coupon.infrastructure.redis.config.CouponIssueProperties;
import com.junsugi.demo.coupon.infrastructure.redis.stream.dlq.CouponIssueDeadLetterService;
import com.junsugi.demo.coupon.infrastructure.redis.stream.message.ClaimedCouponIssueMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponIssuePendingWorker {

    private final CouponIssuePendingMessageClaimer pendingMessageClaimer;
    private final CouponIssueMessageProcessor couponIssueMessageProcessor;
    private final CouponIssueDeadLetterService deadLetterService;
    private final CouponIssueProperties properties;

    public void recoverPendingMessage() {
        List<ClaimedCouponIssueMessage> messages = pendingMessageClaimer.claim();

        if (messages.isEmpty())
            return;

        log.info("Claimed pending coupon issue messages. count={}", messages.size());

        for (ClaimedCouponIssueMessage message : messages) {
            handle(message);
        }
    }

    private void handle(ClaimedCouponIssueMessage message) {
        if (isExceededMaxDeliveryCount(message.deliveryCount())) {
            moveToDeadLetter(message.record(), message.deliveryCount());
            return;
        }

        recover(message.record(), message.deliveryCount());
    }

    private boolean isExceededMaxDeliveryCount(long deliveryCount) {
        return deliveryCount >= properties.pending().maxDeliveryCount();
    }

    private void moveToDeadLetter(MapRecord<String, String, String> record, long deliveryCount) {
        try {
            deadLetterService.moveToDeadLetter(record, deliveryCount);

            pendingMessageClaimer.acknowledge(record.getId());

            log.warn(
                    "Moved coupon issue message to DLQ. recordId={}, deliveryCount={}",
                    record.getId(),
                    deliveryCount
            );
        } catch (Exception e) {
            log.error(
                    "Failed to move coupon issue message to DLQ. recordId={}, deliveryCount={}, error={}",
                    record.getId(),
                    deliveryCount,
                    e.getMessage(),
                    e
            );

            // DLQ 또는 failure_log 저장 실패 시 ACK 하지 않음.
            // 메시지 유실 방지.
        }
    }

    private void recover(MapRecord<String, String, String> record, long deliveryCount) {
        try {
            couponIssueMessageProcessor.process(record.getValue());

            pendingMessageClaimer.acknowledge(record.getId());

            log.info(
                    "Recovered pending coupon issue message. recordId={}, deliveryCount={}",
                    record.getId(),
                    deliveryCount
            );
        } catch (Exception e) {
            log.error(
                    "Failed to recover pending coupon issue message. recordId={}, deliveryCount={}, error={}",
                    record.getId(),
                    deliveryCount,
                    e.getMessage(),
                    e
            );

            // 지금 단계에서는 ACK 하지 않음.
            // 그래야 다시 Pending 상태로 남고 다음 recover 대상이 됨.
        }
    }
}
