package com.junsugi.demo.coupon.presentation;

import com.junsugi.demo.coupon.application.command.CouponCreateCommand;
import com.junsugi.demo.coupon.application.service.CouponSearchService;
import com.junsugi.demo.coupon.application.service.CouponService;
import com.junsugi.demo.coupon.domain.Coupon;
import com.junsugi.demo.coupon.presentation.request.CouponCreateRequest;
import com.junsugi.demo.coupon.presentation.response.CouponCreateResponse;
import com.junsugi.demo.coupon.presentation.response.CouponResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;
    private final CouponSearchService couponSearchService;

    // 쿠폰 생성
    @PostMapping()
    public ResponseEntity<CouponCreateResponse> createCoupon(@Valid @RequestBody CouponCreateRequest request){
        CouponCreateCommand command = request.toCommand();
        CouponCreateResponse response = this.couponService.createCoupon(command);

        return ResponseEntity.ok(response);
    }

    // 전체 쿠폰 조회
    @GetMapping()
    public ResponseEntity<List<CouponResponse>> findCoupons(){
        return ResponseEntity.ok(this.couponSearchService.findCoupons());
    }

    // 특정 쿠폰 조회
    @GetMapping("{couponId}")
    public ResponseEntity<CouponResponse> findCoupon(@PathVariable Long couponId){
        return ResponseEntity.ok(this.couponSearchService.findCoupon(couponId));
    }
}
