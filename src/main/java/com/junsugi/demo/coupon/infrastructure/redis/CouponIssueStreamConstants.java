package com.junsugi.demo.coupon.infrastructure.redis;

public final class CouponIssueStreamConstants {

    public static final String ISSUE_STREAM_KEY = "coupon:issue:stream";
    public static final String ISSUE_GROUP = "coupon-issue-group";

    public static final String ISSUE_PENDING_WORKER_CONSUMER = "coupon-issue-pending-worker-1";

    private CouponIssueStreamConstants() {}
}
