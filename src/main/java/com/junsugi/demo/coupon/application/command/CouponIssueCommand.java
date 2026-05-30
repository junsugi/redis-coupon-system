package com.junsugi.demo.coupon.application.command;

public record CouponIssueCommand (
    Long couponId,
    Long userid
){
}

