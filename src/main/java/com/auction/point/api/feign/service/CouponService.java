package com.auction.point.api.feign.service;

import com.auction.point.api.common.apipayload.ApiResponse;
import com.auction.point.api.config.FeignConfig;
import com.auction.point.api.feign.dto.request.CouponUseRequestDto;
import com.auction.point.api.feign.dto.response.CouponGetResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import static com.auction.point.api.common.constants.Const.USER_ID;

@FeignClient(
        name = "coupon-service",
        url = "${spring.cloud.openfeign.url}",
        configuration = FeignConfig.class
)
public interface CouponService {
    @GetMapping("/v4/coupon/{couponUserId}")
    ApiResponse<CouponGetResponseDto> getValidCoupon(
            @RequestHeader(USER_ID) long userId,
            @PathVariable("couponUserId") Long couponUserId
    );

    @PostMapping("/v4/coupon")
    ApiResponse<Void> useCoupon(
            @RequestHeader(USER_ID) long userId,
            CouponUseRequestDto couponUseRequestDto
    );
}
