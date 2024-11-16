package com.auction.point.api.feign.dto.response;

import lombok.Getter;

@Getter
public class CouponGetResponseDto {
    private long couponUserId;
    private int discountRate;
}
