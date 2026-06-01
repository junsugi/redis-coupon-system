package com.junsugi.demo.coupon.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "coupon_issue_failure_log",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_coupon_issue_failure_log_record_id",
                        columnNames = "original_record_id"
                )
        }
)
public class CouponIssueFailureLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_record_id", nullable = false, length = 100)
    private String originalRecordId;

    @Column(name = "coupon_id", nullable = false)
    private Long couponId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    @Column(name = "failed_at", nullable = false)
    private LocalDateTime failedAt;

    @Column(name = "delivery_count", nullable = false)
    private long deliveryCount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    private CouponIssueFailureLog(
            String originalRecordId,
            Long couponId,
            Long userId,
            LocalDateTime issuedAt,
            String errorMessage,
            LocalDateTime failedAt,
            long deliveryCount
    ) {
        this.originalRecordId = originalRecordId;
        this.couponId = couponId;
        this.userId = userId;
        this.issuedAt = issuedAt;
        this.errorMessage = errorMessage;
        this.failedAt = failedAt;
        this.deliveryCount = deliveryCount;
        this.createdAt = LocalDateTime.now();
    }

    public static CouponIssueFailureLog create(
            String originalRecordId,
            Long couponId,
            Long userId,
            LocalDateTime issuedAt,
            String errorMessage,
            LocalDateTime failedAt,
            long deliveryCount
    ) {
        return new CouponIssueFailureLog(
                originalRecordId,
                couponId,
                userId,
                issuedAt,
                errorMessage,
                failedAt,
                deliveryCount
        );
    }
}