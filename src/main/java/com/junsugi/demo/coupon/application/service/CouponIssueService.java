package com.junsugi.demo.coupon.application.service;

import com.junsugi.demo.coupon.application.command.CouponCreateCommand;
import com.junsugi.demo.coupon.presentation.response.CouponCreateResponse;

public interface CouponIssueService {
    CouponCreateResponse createCoupon(CouponCreateCommand command);
}
