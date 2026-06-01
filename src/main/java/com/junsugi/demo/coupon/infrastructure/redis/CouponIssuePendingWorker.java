package com.junsugi.demo.coupon.infrastructure.redis;

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

    public void recoverPendingMessage(){
        List<MapRecord<String, String, String>> messages = pendingMessageClaimer.claim();

        if (messages.isEmpty())
            return;

        log.info("Claimed pending coupon issue messages. count={}", messages.size());

        for (MapRecord<String, String, String> message : messages)
            recover(message);
    }

    private void recover(MapRecord<String, String, String> message) {
        try {
            couponIssueMessageProcessor.process(message.getValue());

            pendingMessageClaimer.acknowledge(message.getId());

            log.info("Recovered pending coupon issue message. recordId={}", message.getId());
        } catch (Exception e) {
            log.error("Failed to recover pending coupon issue message. recordId={}, error={}",
                    message.getId(), e.getMessage(), e);

            // 지금 단계에서는 ACK 하지 않음.
            // 그래야 다시 Pending 상태로 남고 다음 recover 대상이 됨.
            // 다음 단계에서 retry count / DLQ 정책을 붙일 예정.
        }
    }
}
