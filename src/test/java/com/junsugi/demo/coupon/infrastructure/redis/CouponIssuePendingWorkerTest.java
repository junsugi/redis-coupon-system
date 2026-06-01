package com.junsugi.demo.coupon.infrastructure.redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.*;

@Testcontainers
@SpringBootTest(properties = {
        "coupon.issue.stream.listener.enabled=false",
        "coupon.issue.pending-worker.enabled=false",
        "coupon.issue.stream.pending.min-idle-time=0ms",
        "coupon.issue.stream.pending.claim-count=10"
})
public class CouponIssuePendingWorkerTest {

    private static final String STREAM_KEY = CouponIssueStreamConstants.ISSUE_STREAM_KEY;
    private static final String GROUP = CouponIssueStreamConstants.ISSUE_GROUP;
    private static final String DEAD_CONSUMER = "dead-consumer";

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(
            DockerImageName.parse("redis:7-alpine")
    ).withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private CouponIssuePendingWorker pendingWorker;

    @MockitoBean
    private CouponIssueMessageProcessor couponIssueMessageProcessor;

    @BeforeEach
    void setUp() {
        redisTemplate.delete(STREAM_KEY);
        createGroup();
    }

    @Test
    void 처리_중_실패하면_ACK하지_않고_Pending_상태로_남긴다() throws Exception {
        // given
        addIssueMessage();
        readByDeadConsumerWithoutAck();

        Long beforePendingCount = getPendingCount();
        assertThat(beforePendingCount).isEqualTo(1L);

        willThrow(new RuntimeException("DB save failed"))
                .given(couponIssueMessageProcessor)
                .process(anyMap());

        Thread.sleep(200);

        // when
        pendingWorker.recoverPendingMessage();

        // then
        verify(couponIssueMessageProcessor, times(1))
                .process(anyMap());

        long pendingCount = getPendingCount();

        assertThat(pendingCount).isEqualTo(1L);
    }

    @Test
    void 처리에_성공하면_ACK하고_Pending_상태에서_제거된다() throws Exception {
        // given
        addIssueMessage();
        readByDeadConsumerWithoutAck();

        Long beforePendingCount = getPendingCount();
        assertThat(beforePendingCount).isEqualTo(1L);

        Thread.sleep(200);

        // when
        pendingWorker.recoverPendingMessage();

        // then
        verify(couponIssueMessageProcessor, times(1))
                .process(anyMap());

        Long afterPendingCount = getPendingCount();
        assertThat(afterPendingCount).isEqualTo(0L);
    }


    private void addIssueMessage() {
        redisTemplate.opsForStream().add(
                StreamRecords.mapBacked(Map.of(
                        "couponId", "1",
                        "userId", "100",
                        "issuedAt", LocalDateTime.now().toString()
                )).withStreamKey(STREAM_KEY)
        );
    }

    private void readByDeadConsumerWithoutAck() {
        redisTemplate.opsForStream().read(
                Consumer.from(GROUP, DEAD_CONSUMER),
                StreamReadOptions.empty().count(1),
                StreamOffset.create(
                        STREAM_KEY,
                        ReadOffset.lastConsumed()
                )
        );

        // 일부러 ACK 하지 않는다.
        // 이 상태가 Pending 메시지다.
    }

    private void createGroup() {
        redisTemplate.execute((RedisCallback<Object>)  connection -> {
            connection.execute(
                    "XGROUP",
                    bytes("CREATE"),
                    bytes(STREAM_KEY),
                    bytes(GROUP),
                    bytes("0-0"),
                    bytes("MKSTREAM")
            );

            return null;
        });
    }

    private Long getPendingCount() {
        return redisTemplate.opsForStream()
                .pending(STREAM_KEY, GROUP)
                .getTotalPendingMessages();
    }

    private byte[] bytes(String value) {
        return value.getBytes(StandardCharsets.UTF_8);
    }
}
