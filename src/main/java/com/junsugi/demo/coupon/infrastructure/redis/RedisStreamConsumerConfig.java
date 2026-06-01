package com.junsugi.demo.coupon.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

import java.net.InetAddress;
import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class RedisStreamConsumerConfig {

    private final StringRedisTemplate redisTemplate;
    private final CouponIssueStreamListener couponIssueStreamListener;

    @Bean
    @ConditionalOnProperty(
            name = "coupon.issue.stream.listener.enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    public StreamMessageListenerContainer<String, MapRecord<String, String, String>> couponIssueStreamContainer(
            RedisConnectionFactory connectionFactory
    ){
        createStreamIfNotExists(connectionFactory);
        createGroupIfNotExists();

        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options =
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                        .builder()
                        .pollTimeout(Duration.ofSeconds(1))
                        .batchSize(10)
                        .build();

        StreamMessageListenerContainer<String, MapRecord<String, String, String>> container =
                StreamMessageListenerContainer.create(connectionFactory, options);

        container.receive(
                Consumer.from(
                        CouponIssueStreamConstants.ISSUE_GROUP,
                        getConsumerName()
                ),
                StreamOffset.create(
                        CouponIssueStreamConstants.ISSUE_STREAM_KEY,
                        ReadOffset.lastConsumed()
                ),
                couponIssueStreamListener
        );

        container.start();

        return container;
    }

    private void createStreamIfNotExists(RedisConnectionFactory connectionFactory) {
        try {
            connectionFactory.getConnection()
                    .streamCommands()
                    .xGroupCreate(
                            CouponIssueStreamConstants.ISSUE_STREAM_KEY.getBytes(),
                            CouponIssueStreamConstants.ISSUE_GROUP,
                            ReadOffset.from("0-0"),
                            true
                    );
        } catch (DataAccessException e) {
            String message = e.getCause().getMessage();
            if (message != null && message.contains("BUSYGROUP")) {
                return;
            }

            throw e;
        }
    }

    private void createGroupIfNotExists() {
        try {
            redisTemplate.opsForStream().createGroup(
                    CouponIssueStreamConstants.ISSUE_STREAM_KEY,
                    ReadOffset.from("0-0"),
                    CouponIssueStreamConstants.ISSUE_GROUP
            );
        } catch (DataAccessException e){
            String message = e.getCause().getMessage();

            if (message != null && message.contains("BUSYGROUP")) {
                return;
            }

            throw e;
        }
    }

    private String getConsumerName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "coupon-consumer";
        }
    }
}
