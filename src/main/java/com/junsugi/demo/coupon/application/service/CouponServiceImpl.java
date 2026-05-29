package com.junsugi.demo.coupon.application.service;

import com.junsugi.demo.coupon.application.command.CouponCreateCommand;
import com.junsugi.demo.coupon.domain.Coupon;
import com.junsugi.demo.coupon.infrastructure.CouponRepository;
import com.junsugi.demo.coupon.presentation.mapper.CouponMapper;
import com.junsugi.demo.coupon.presentation.response.CouponCreateResponse;
import com.junsugi.demo.coupon.presentation.response.CouponResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService, CouponSearchService {

    private final CouponRepository couponRepository;

    @Override
    @Transactional
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

    @Override
    @Transactional(readOnly = true)
    public List<CouponResponse> findCoupons() {
        List<Coupon> coupons = couponRepository.findAll();

        return coupons.stream()
                .map(CouponMapper::toResponse)
                .toList();
    }
}
