package com.auction.point.api.domain.point.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConvertResponseDto {
    private int toCashAmount;
    private int balanceAmount;
}
