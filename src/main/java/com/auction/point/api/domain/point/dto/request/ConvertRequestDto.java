package com.auction.point.api.domain.point.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ConvertRequestDto {
    @Min(value = 1000, message = "1000 포인트 이상부터 전환 가능합니다.")
    private int amount;
    @NotBlank(message = "은행명 필수입니다.")
    private String bankCode;
    @NotBlank(message = "계좌번호는 필수입니다.")
    private String bankAccount;
}
