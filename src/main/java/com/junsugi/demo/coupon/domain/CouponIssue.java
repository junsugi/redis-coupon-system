package com.junsugi.demo.coupon.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
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
    private LocalDateTime createAt;

    private CouponIssue(
            Long couponId,
            Long userId
    ) {
        this.couponId = couponId;
        this.userId = userId;
        this.createAt = LocalDateTime.now();
    }

    public static CouponIssue create(
            Long couponId,
            Long userId
    ) {
        return new CouponIssue(couponId, userId);
    }
}
