package com.junsugi.demo.coupon.presentation;

import com.junsugi.demo.coupon.application.command.CouponCreateCommand;
import com.junsugi.demo.coupon.application.service.CouponIssueService;
import com.junsugi.demo.coupon.presentation.request.CouponCreateRequest;
import com.junsugi.demo.coupon.presentation.response.CouponCreateResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CouponController {

    private final CouponIssueService couponIssueService;

    // 쿠폰 생성
    @PostMapping("/api/coupons")
    public ResponseEntity<CouponCreateResponse> createCoupon(@Valid @RequestBody CouponCreateRequest request){
        CouponCreateCommand command = request.toCommand();
        CouponCreateResponse response = this.couponIssueService.createCoupon(command);

        return ResponseEntity.ok(response);
    }
}
