package com.junsugi.demo.coupon.presentation.request;

import com.junsugi.demo.coupon.application.command.CouponIssueCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CouponIssueRequest (
        @NotNull(message = "사용자 ID는 필수입니다.")
        @Positive(message = "사용자 ID는 양수여야 합니다.")
        Long userId
){
    public CouponIssueCommand toCommand(Long couponId) {
        return new CouponIssueCommand(couponId, userId);
    }
}
