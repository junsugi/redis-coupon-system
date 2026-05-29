package com.junsugi.demo.coupon.presentation.request;

import com.junsugi.demo.coupon.application.command.CouponCreateCommand;
import com.junsugi.demo.coupon.domain.DiscountType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CouponCreateRequest(
        @NotBlank(message = "쿠폰 이름은 필수입니다.")
        String name,

        @NotNull(message = "총 발급 수량은 필수입니다.")
        @Min(value = 1, message = "총 발급 수량은 1개 이상이어야 합니다.")
        Integer totalQuantity,

        @NotNull(message = "할인 타입은 필수입니다.")
        DiscountType discountType,

        @NotNull(message = "할인 값은 필수입니다.")
        @Min(value = 1, message = "할인 값은 1 이상이어야 합니다.")
        Integer discountValue,

        @NotNull(message = "발급 시작 시간은 필수입니다.")
        LocalDateTime issueStartAt,

        @NotNull(message = "발급 종료 시간은 필수입니다.")
        LocalDateTime issueEndAt
) {
    public CouponCreateCommand toCommand() {
        return new CouponCreateCommand(
                name,
                totalQuantity,
                discountType,
                discountValue,
                issueStartAt,
                issueEndAt
        );
    }
}


