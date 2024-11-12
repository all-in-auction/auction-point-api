package com.auction.point.api.domain.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChargeResponseDto {
    private int paymentAmount;
    private int chargedAmount;
    private int balanceAmount;
}
