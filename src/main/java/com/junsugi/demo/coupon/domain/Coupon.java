package com.junsugi.demo.coupon.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int totalQuantity;

    @Column(nullable = false)
    private int issuedQuantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType discountType;

    @Column(nullable = false)
    private int discountValue;

    @Column(nullable = false)
    private LocalDateTime issueStartAt;

    @Column(nullable = false)
    private LocalDateTime issueEndAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private Coupon(
            String name,
            int totalQuantity,
            DiscountType discountType,
            int discountValue,
            LocalDateTime issueStartAt,
            LocalDateTime issueEndAt
    ) {
        validateIssuePeriod(issueStartAt, issueEndAt);

        this.name = name;
        this.totalQuantity = totalQuantity;
        this.issuedQuantity = 0;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.issueStartAt = issueStartAt;
        this.issueEndAt = issueEndAt;
        this.status = CouponStatus.ACTIVE;
    }

    public static Coupon create(
            String name,
            int totalQuantity,
            DiscountType discountType,
            int discountValue,
            LocalDateTime issueStartAt,
            LocalDateTime issueEndAt
    ) {
        return new Coupon(
                name,
                totalQuantity,
                discountType,
                discountValue,
                issueStartAt,
                issueEndAt
        );
    }

    private void validateIssuePeriod(LocalDateTime startAt, LocalDateTime endAt) {
        if (endAt.isBefore(startAt) || endAt.isEqual(startAt))
            throw new IllegalArgumentException("쿠폰 발급 종료 시간은 시작 시간보다 이후여야 합니다.");
    }
}
