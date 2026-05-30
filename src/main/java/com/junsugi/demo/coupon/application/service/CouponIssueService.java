package com.junsugi.demo.coupon.application.service;

import com.junsugi.demo.coupon.application.command.CouponIssueCommand;
import com.junsugi.demo.coupon.presentation.response.CouponIssueResponse;

public interface CouponIssueService {
    CouponIssueResponse issue(CouponIssueCommand command);
}
