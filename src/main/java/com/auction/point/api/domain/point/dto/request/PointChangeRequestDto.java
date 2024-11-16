package com.auction.point.api.domain.point.dto.request;

import com.auction.point.api.domain.pointHistory.enums.PaymentType;
import lombok.Getter;

@Getter
public class PointChangeRequestDto {
    private int amount;
    private PaymentType paymentType;
}
