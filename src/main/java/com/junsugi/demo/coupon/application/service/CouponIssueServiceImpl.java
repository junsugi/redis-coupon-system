package com.junsugi.demo.coupon.application.service;

import com.junsugi.demo.coupon.application.command.CouponCreateCommand;
import com.junsugi.demo.coupon.domain.Coupon;
import com.junsugi.demo.coupon.infrastructure.CouponRepository;
import com.junsugi.demo.coupon.presentation.response.CouponCreateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponIssueServiceImpl implements CouponIssueService{

    private final CouponRepository couponRepository;

    @Override
    public CouponCreateResponse createCoupon(CouponCreateCommand command) {
        Coupon coupon = Coupon.create(
                command.name(),
                command.totalQuantity(),
                command.discountType(),
                command.discountValue(),
                command.issueStartAt(),
                command.issueEndAt()
        );
        Coupon saveCoupon = couponRepository.save(coupon);

        return CouponCreateResponse.from(saveCoupon);
    }
}
