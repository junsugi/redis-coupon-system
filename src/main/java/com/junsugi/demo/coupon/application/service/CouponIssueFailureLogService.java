package com.junsugi.demo.coupon.application.service;

import com.junsugi.demo.coupon.application.command.CouponIssueFailureLogCommand;
import com.junsugi.demo.coupon.domain.CouponIssueFailureLog;
import com.junsugi.demo.coupon.infrastructure.persistence.CouponIssueFailureLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponIssueFailureLogService {

    private final CouponIssueFailureLogRepository failureLogRepository;

    @Transactional
    public void saveIfAbsent(CouponIssueFailureLogCommand command) {
        if (failureLogRepository.existsByOriginalRecordId(command.originalRecordId())) {
            return;
        }

        try {
            CouponIssueFailureLog failureLog = CouponIssueFailureLog.create(
                    command.originalRecordId(),
                    command.couponId(),
                    command.userId(),
                    command.issuedAt(),
                    command.errorMessage(),
                    command.failedAt(),
                    command.deliveryCount()
            );

            failureLogRepository.save(failureLog);
        } catch (DataIntegrityViolationException e) {
            // 동시에 같은 originalRecordId가 저장된 경우.
            // 이미 실패 로그가 저장된 상태로 보고 성공 처리한다.
        }
    }
}