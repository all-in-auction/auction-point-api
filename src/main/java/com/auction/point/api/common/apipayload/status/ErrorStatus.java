package com.auction.point.api.common.apipayload.status;

import com.auction.point.api.common.apipayload.BaseCode;
import com.auction.point.api.common.apipayload.dto.ReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseCode {

    // common
    _INVALID_REQUEST(HttpStatus.BAD_REQUEST, "400", "잘못된 요청입니다."),
    _PERMISSION_DENIED(HttpStatus.FORBIDDEN, "403", "권한이 없습니다."),
    _TIMEOUT(HttpStatus.REQUEST_TIMEOUT, "408", "잠시 후 다시 시도해주세요."),
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "500", "요청에 실패하였습니다."),

    //Auth
    _NOT_AUTHENTICATIONPRINCIPAL_USER(HttpStatus.UNAUTHORIZED, "401", "인증되지 않은 유저입니다."),
    _NOT_FOUND_USER(HttpStatus.NOT_FOUND, "404", "권한이 없습니다."),

    // coupon
    _NOT_FOUND_COUPON(HttpStatus.NOT_FOUND, "404", "해당 쿠폰을 찾을 수 없습니다."),
    _NOT_OWNED_COUPON(HttpStatus.BAD_REQUEST, "400", "사용자에게 해당 쿠폰이 없습니다."),
    _ALREADY_USED_COUPON(HttpStatus.BAD_REQUEST, "400", "이미 사용된 쿠폰입니다."),
    _EXPIRED_COUPON(HttpStatus.BAD_REQUEST, "400", "유효기간이 지난 쿠폰입니다."),
    _SOLD_OUT_COUPON(HttpStatus.CONFLICT, "409", "준비된 쿠폰 수량이 모두 소진되었습니다."),
    _ALREADY_CLAIMED_COUPON(HttpStatus.BAD_REQUEST, "400", "이미 수령한 쿠폰입니다."),
    _INTERNAL_SERVER_ERROR_COUPON(HttpStatus.INTERNAL_SERVER_ERROR, "500", "쿠폰 발급에 실패하였습니다."),

    // pay
    _INVALID_AMOUNT_REQUEST(HttpStatus.BAD_REQUEST, "400", "결제 금액은 1000원 단위입니다."),
    _INVALID_PAY_REQUEST(HttpStatus.BAD_REQUEST, "400", "잘못된 거래 승인 요청입니다."),
    _INVALID_CONVERT_REQUEST(HttpStatus.BAD_REQUEST, "400", "현재 포인트 잔고보다 더 큰 값을 전환 요청할 수 없습니다."),
    ;

    private HttpStatus httpStatus;
    private String statusCode;
    private String message;

    public static ErrorStatus getErrorStatus(String findMessage) {
        return Arrays.stream(values())
                .filter(errorStatus -> errorStatus.message.equals(findMessage))
                .findAny()
                .orElse(ErrorStatus._INTERNAL_SERVER_ERROR);
    }

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
