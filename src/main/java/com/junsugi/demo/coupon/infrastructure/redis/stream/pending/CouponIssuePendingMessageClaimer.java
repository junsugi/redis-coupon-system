package com.junsugi.demo.coupon.infrastructure.redis.stream.pending;

import com.junsugi.demo.coupon.infrastructure.redis.config.CouponIssueProperties;
import com.junsugi.demo.coupon.infrastructure.redis.stream.message.ClaimedCouponIssueMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.PendingMessage;
import org.springframework.data.redis.connection.stream.PendingMessages;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.junsugi.demo.coupon.infrastructure.redis.CouponIssueStreamConstants.*;

@Component
@RequiredArgsConstructor
public class CouponIssuePendingMessageClaimer {

    private final StringRedisTemplate redisTemplate;
    private final CouponIssueProperties properties;

    public List<ClaimedCouponIssueMessage> claim() {
        StreamOperations<String, String, String> streamOps = redisTemplate.opsForStream();

        Duration minIdleTime = properties.pending().minIdleTime();
        int claimCount = properties.pending().claimCount();

        PendingMessages pendingMessages = streamOps.pending(
                ISSUE_STREAM_KEY,
                ISSUE_GROUP,
                Range.unbounded(),
                claimCount
        );

        if (pendingMessages == null || pendingMessages.isEmpty())
            return List.of();

        Map<RecordId, Long> deliveryCountByRecordId =
                extractClaimableRecordIds(pendingMessages, minIdleTime);

        if (deliveryCountByRecordId.isEmpty())
            return List.of();

        RecordId[] recordIds = deliveryCountByRecordId.keySet().toArray(RecordId[]::new);

        List<MapRecord<String, String, String>> claimedRecords = streamOps.claim(
                ISSUE_STREAM_KEY,
                ISSUE_GROUP,
                ISSUE_PENDING_WORKER_CONSUMER,
                minIdleTime,
                recordIds
        );

        if (claimedRecords == null || claimedRecords.isEmpty())
            return List.of();

        List<ClaimedCouponIssueMessage> result = new ArrayList<>();
        for (MapRecord<String, String, String> record : claimedRecords) {
            long deliveryCount = deliveryCountByRecordId.getOrDefault(record.getId(), 1L);
            result.add(new ClaimedCouponIssueMessage(record, deliveryCount));
        }

        return result;
    }

    public void acknowledge(RecordId recordId) {
        redisTemplate.opsForStream().acknowledge(
                ISSUE_STREAM_KEY,
                ISSUE_GROUP,
                recordId
        );
    }

    private Map<RecordId, Long> extractClaimableRecordIds(
            PendingMessages pendingMessages,
            Duration minIdleTime
    ) {
        Map<RecordId, Long> deliveryCountByRecordId = new LinkedHashMap<>();

        for (PendingMessage pendingMessage : pendingMessages) {
            if (pendingMessage.getElapsedTimeSinceLastDelivery().compareTo(minIdleTime) >= 0) {
                deliveryCountByRecordId.put(
                        pendingMessage.getId(),
                        pendingMessage.getTotalDeliveryCount()
                );
            }
        }

        return deliveryCountByRecordId;
    }
}
