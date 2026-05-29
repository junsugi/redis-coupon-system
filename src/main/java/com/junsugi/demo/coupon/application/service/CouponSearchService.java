package com.junsugi.demo.coupon.application.service;

import com.junsugi.demo.coupon.presentation.response.CouponResponse;

import java.util.List;

public interface CouponSearchService {
    List<CouponResponse> findCoupons();
}
