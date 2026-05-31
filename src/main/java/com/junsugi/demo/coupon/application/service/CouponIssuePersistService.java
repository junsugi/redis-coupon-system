package com.junsugi.demo.coupon.application.service;

import com.junsugi.demo.coupon.application.command.CouponIssuePersistCommand;
import com.junsugi.demo.coupon.application.port.CouponIssueRepository;
import com.junsugi.demo.coupon.domain.CouponIssue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponIssuePersistService {

    private final CouponIssueRepository couponIssueRepository;

    @Transactional
    public void persist(CouponIssuePersistCommand command) {
        CouponIssue couponIssue = CouponIssue.create(
                command.couponId(),
                command.userId(),
                command.issuedAt()
        );

        this.couponIssueRepository.saveIfAbsent(couponIssue);
    }
}
