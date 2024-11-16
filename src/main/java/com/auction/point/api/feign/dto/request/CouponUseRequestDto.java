package com.auction.point.api.feign.dto.request;

import com.auction.point.api.domain.payment.entity.Payment;
import com.auction.point.api.domain.pointHistory.entity.PointHistory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponUseRequestDto {
    private long pointHistoryId;

    public static CouponUseRequestDto from(PointHistory pointHistory) {
        return new CouponUseRequestDto(pointHistory.getId());
    }
}
