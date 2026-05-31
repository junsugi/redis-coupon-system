package com.junsugi.demo.coupon.infrastructure.persistence;

import com.junsugi.demo.coupon.application.port.CouponIssueRepository;
import com.junsugi.demo.coupon.domain.CouponIssue;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CouponIssueRepositoryAdapter implements CouponIssueRepository {

    private final CouponIssueJpaRepository couponIssueJpaRepository;

    @Override
    public CouponIssue save(CouponIssue couponIssue) {
        return couponIssueJpaRepository.save(couponIssue);
    }

    @Override
    public void saveIfAbsent(CouponIssue couponIssue) {
        try {
            couponIssueJpaRepository.save(couponIssue);
        } catch (DataIntegrityViolationException e) {
            // Consumer 재처리로 인한 중복 저장은 성공으로 간주
        }
    }
}
