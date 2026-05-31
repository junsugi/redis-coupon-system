package com.junsugi.demo.coupon.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(
        name = "coupon_issue",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_coupon_issue_coupon_user",
                        columnNames = {"coupon_id", "user_id"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponIssue {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Long couponId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime issuedAt;

    private CouponIssue(Long couponId, Long userId, LocalDateTime issuedAt) {
        this.couponId = couponId;
        this.userId = userId;
        this.issuedAt = issuedAt;
    }

    public static CouponIssue create(Long couponId, Long userId, LocalDateTime issuedAt) {
        return new CouponIssue(
                couponId,
                userId,
                issuedAt
        );
    }
}
