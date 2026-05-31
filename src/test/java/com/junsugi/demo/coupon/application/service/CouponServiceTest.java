package com.junsugi.demo.coupon.application.service;

import com.junsugi.demo.coupon.application.command.CouponIssueCommand;
import com.junsugi.demo.coupon.application.port.CouponIssueLimiter;
import com.junsugi.demo.coupon.domain.Coupon;
import com.junsugi.demo.coupon.infrastructure.persistence.CouponRepository;
import com.junsugi.demo.coupon.presentation.response.CouponIssueResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private CouponIssueLimiter couponIssueLimiter;

    @InjectMocks
    private CouponServiceImpl couponService;

    @Test
    void 존재하지_않은_쿠폰이면_발행에_실패한다(){
        Long couponId = 9999L;
        Long userId = 1L;
        CouponIssueCommand command = new CouponIssueCommand(couponId, userId);

        given(couponRepository.findById(couponId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> couponService.issue(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 쿠폰입니다.");

        verify(couponIssueLimiter, never()).issue(anyLong(), anyLong(), anyInt(), any(LocalDateTime.class));
    }

    @Test
    void 유효하지_않은_쿠폰이면_발행에_실패한다(){
        Long couponId = 1L;
        Long userId = 1L;
        CouponIssueCommand command = new CouponIssueCommand(couponId, userId);
        Coupon coupon = mock(Coupon.class);

        given(couponRepository.findById(command.couponId()))
                .willReturn(Optional.of(coupon));

        willThrow(new IllegalArgumentException("비활성화된 쿠폰입니다."))
                .given(coupon)
                .validateIssuable();

        assertThatThrownBy(() -> couponService.issue(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비활성화된 쿠폰입니다.");

        verify(coupon).validateIssuable();

        verify(couponIssueLimiter, never()).issue(anyLong(), anyLong(), anyInt(), any(LocalDateTime.class));
    }

    @Test
    void 쿠폰_발행에_성공한다(){
        Long couponId = 1L;
        Long userId = 1L;
        int totalQuantity = 100;

        CouponIssueCommand command = new CouponIssueCommand(couponId, userId);
        Coupon coupon = mock(Coupon.class);

        given(couponRepository.findById(command.couponId()))
                .willReturn(Optional.of(coupon));

        given(coupon.getId()).willReturn(couponId);
        given(coupon.getTotalQuantity()).willReturn(totalQuantity);

        CouponIssueResponse response = couponService.issue(command);

        verify(coupon).validateIssuable();
        verify(couponIssueLimiter).issue(
                eq(couponId),
                eq(userId),
                eq(totalQuantity),
                any(LocalDateTime.class)
        );

        assertThat(response.couponId()).isEqualTo(couponId);
        assertThat(response.userId()).isEqualTo(userId);
        assertThat(response.issuedAt()).isNotNull();
        assertThat(response.status()).isEqualTo("ISSUED");
    }
}
