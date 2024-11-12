package com.auction.point.api.feign.dto.request;

import com.auction.point.api.domain.payment.entity.Payment;
import com.auction.point.api.domain.pointHistory.entity.PointHistory;
import lombok.Getter;

@Getter
public class CouponUseRequestDto {
    private long couponUserId;
    private long pointHistoryId;

    private CouponUseRequestDto(long couponUserId, long pointHistoryId) {
        this.couponUserId = couponUserId;
        this.pointHistoryId = pointHistoryId;
    }

    public static CouponUseRequestDto from(Payment payment, PointHistory pointHistory) {
        return new CouponUseRequestDto(payment.getCouponUserId(), pointHistory.getId());
    }
}
