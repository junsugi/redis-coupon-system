package com.junsugi.demo.coupon.application.service;

import com.junsugi.demo.coupon.application.command.CouponCreateCommand;
import com.junsugi.demo.coupon.application.command.CouponIssueCommand;
import com.junsugi.demo.coupon.application.port.CouponIssueLimiter;
import com.junsugi.demo.coupon.domain.Coupon;
import com.junsugi.demo.coupon.domain.CouponIssue;
import com.junsugi.demo.coupon.infrastructure.repository.CouponIssueRepository;
import com.junsugi.demo.coupon.infrastructure.repository.CouponRepository;
import com.junsugi.demo.coupon.presentation.mapper.CouponMapper;
import com.junsugi.demo.coupon.presentation.response.CouponCreateResponse;
import com.junsugi.demo.coupon.presentation.response.CouponIssueResponse;
import com.junsugi.demo.coupon.presentation.response.CouponResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService, CouponSearchService, CouponIssueService {

    private final CouponIssueLimiter couponIssueLimiter;

    private final CouponRepository couponRepository;
    private final CouponIssueRepository couponIssueRepository;

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
        Coupon saveCoupon = this.couponRepository.save(coupon);

        return CouponCreateResponse.from(saveCoupon);
    }

    @Override
    public CouponResponse findCoupon(Long couponId) {
        Coupon coupon = this.couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));

        return CouponMapper.toResponse(coupon);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponResponse> findCoupons() {
        List<Coupon> coupons = this.couponRepository.findAll();

        return coupons.stream()
                .map(CouponMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CouponIssueResponse issue(CouponIssueCommand command) {
        Coupon coupon = this.couponRepository.findById(command.couponId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰입니다."));

        coupon.validateIssuable();

        this.couponIssueLimiter.issue(
                coupon.getId(),
                command.userid(),
                coupon.getTotalQuantity()
        );

        CouponIssue couponIssue = CouponIssue.create(
                coupon.getId(),
                command.userid()
        );

        CouponIssue issuedCoupon = this.couponIssueRepository.save(couponIssue);

        return CouponIssueResponse.from(issuedCoupon);
    }
}
