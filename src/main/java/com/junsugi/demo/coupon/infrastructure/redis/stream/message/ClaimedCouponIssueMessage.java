package com.junsugi.demo.coupon.infrastructure.redis.stream.message;

import org.springframework.data.redis.connection.stream.MapRecord;

public record ClaimedCouponIssueMessage (
        MapRecord<String, String, String> record,
        long deliveryCount
){
}
