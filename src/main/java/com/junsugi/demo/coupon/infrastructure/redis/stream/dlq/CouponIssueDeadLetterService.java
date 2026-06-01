package com.junsugi.demo.coupon.infrastructure.redis.stream.dlq;

import com.junsugi.demo.coupon.application.command.CouponIssueFailureLogCommand;
import com.junsugi.demo.coupon.application.service.CouponIssueFailureLogService;
import com.junsugi.demo.coupon.infrastructure.redis.stream.message.CouponIssueStreamMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CouponIssueDeadLetterService {

    private static final String MAX_DELIVERY_COUNT_EXCEEDED =
            "MAX_DELIVERY_COUNT_EXCEEDED";

    private final CouponIssueFailureLogService failureLogService;
    private final CouponIssueDeadLetterPublisher deadLetterPublisher;

    public void moveToDeadLetter(MapRecord<String, String, String> record, long deliveryCount) {
        CouponIssueStreamMessage message = CouponIssueStreamMessage.from(record.getValue());
        LocalDateTime failedAt = LocalDateTime.now();

        failureLogService.saveIfAbsent(
                new CouponIssueFailureLogCommand(
                        record.getId().getValue(),
                        message.couponId(),
                        message.userId(),
                        message.issuedAt(),
                        MAX_DELIVERY_COUNT_EXCEEDED,
                        failedAt,
                        deliveryCount
                )
        );

        deadLetterPublisher.publish(
                new CouponIssueDeadLetterCommand(
                        record.getId().getValue(),
                        message.couponId(),
                        message.userId(),
                        message.issuedAt(),
                        MAX_DELIVERY_COUNT_EXCEEDED,
                        failedAt,
                        deliveryCount
                )
        );
    }
}
