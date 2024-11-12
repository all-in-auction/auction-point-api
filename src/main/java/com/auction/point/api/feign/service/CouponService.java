package com.auction.point.api.feign.service;

import com.auction.point.api.common.apipayload.ApiResponse;
import com.auction.point.api.feign.dto.request.CouponUseRequestDto;
import com.auction.point.api.feign.dto.response.CouponGetResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "coupon-service",
        url = "http://localhost:8080"
)
public interface CouponService {
    @GetMapping("/api/internal/v4/coupon/{couponId}")
    ApiResponse<CouponGetResponseDto> getCoupon(
            @RequestHeader("X-User-Agent") long userId,
            @PathVariable("couponId") Long couponId
    );

    @PostMapping("/api/internal/v4/coupon")
    ApiResponse<Void> useCoupon(
            @RequestHeader("X-User-Agent") long userId,
            CouponUseRequestDto couponUseRequestDto
    );
}
