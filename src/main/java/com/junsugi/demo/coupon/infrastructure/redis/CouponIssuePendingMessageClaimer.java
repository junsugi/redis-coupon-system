package com.junsugi.demo.coupon.infrastructure.redis;

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
import java.util.List;

import static com.junsugi.demo.coupon.infrastructure.redis.CouponIssueStreamConstants.*;

@Component
@RequiredArgsConstructor
public class CouponIssuePendingMessageClaimer {

    private final StringRedisTemplate redisTemplate;
    private final CouponIssuePendingProperties pendingProperties;

    public List<MapRecord<String, String, String>> claim() {
        StreamOperations<String, String, String> streamOps = redisTemplate.opsForStream();

        Duration minIdleTime = pendingProperties.minIdleTime();
        int claimCount = pendingProperties.claimCount();

        PendingMessages pendingMessages = streamOps.pending(
                ISSUE_STREAM_KEY,
                ISSUE_GROUP,
                Range.unbounded(),
                claimCount
        );

        if (pendingMessages == null || pendingMessages.isEmpty())
            return List.of();

        RecordId[] recordIds = extractClaimableRecordIds(pendingMessages, minIdleTime);

        if (recordIds.length == 0)
            return List.of();

        List<MapRecord<String, String, String>> claimedMessages = streamOps.claim(
                ISSUE_STREAM_KEY,
                ISSUE_GROUP,
                ISSUE_PENDING_WORKER_CONSUMER,
                minIdleTime,
                recordIds
        );

        if (claimedMessages == null || claimedMessages.isEmpty())
            return List.of();

        return claimedMessages;
    }

    public void acknowledge(RecordId recordId) {
        redisTemplate.opsForStream().acknowledge(
                ISSUE_STREAM_KEY,
                ISSUE_GROUP,
                recordId
        );
    }

    private RecordId[] extractClaimableRecordIds(
            PendingMessages pendingMessages,
            Duration minIdleTime
    ) {
        List<RecordId> recordIds = new ArrayList<>();

        for (PendingMessage pendingMessage : pendingMessages) {
            if (pendingMessage.getElapsedTimeSinceLastDelivery().compareTo(minIdleTime) >= 0) {
                recordIds.add(pendingMessage.getId());
            }
        }

        return recordIds.toArray(RecordId[]::new);
    }
}
