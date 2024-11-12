package com.auction.point.api.common.apipayload.status;

import com.auction.point.api.common.apipayload.BaseCode;
import com.auction.point.api.common.apipayload.dto.ReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseCode {

    // common
    _INVALID_REQUEST(HttpStatus.NOT_FOUND, "404", "잘못된 요청입니다."),
    _PERMISSION_DENIED(HttpStatus.BAD_REQUEST, "404", "권한이 없습니다."),

    //Auth
    _NOT_AUTHENTICATIONPRINCIPAL_USER(HttpStatus.UNAUTHORIZED, "401", "인증되지 않은 유저입니다."),
    _NOT_FOUND_USER(HttpStatus.NOT_FOUND, "404", "권한이 없습니다."),

    // pay
    _INVALID_AMOUNT_REQUEST(HttpStatus.BAD_REQUEST, "400", "결제 금액은 1000원 단위입니다."),
    _INVALID_PAY_REQUEST(HttpStatus.BAD_REQUEST, "400", "잘못된 거래 승인 요청입니다."),
    _INVALID_CONVERT_REQUEST(HttpStatus.BAD_REQUEST, "400", "현재 포인트 잔고보다 더 큰 값을 전환 요청할 수 없습니다."),
    ;

    private HttpStatus httpStatus;
    private String statusCode;
    private String message;


    @Override
    public ReasonDto getReasonHttpStatus() {
        return ReasonDto.builder()
                .statusCode(statusCode)
                .message(message)
                .httpStatus(httpStatus)
                .success(false)
                .build();
    }
}
