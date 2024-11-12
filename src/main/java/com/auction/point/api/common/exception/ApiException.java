package com.auction.point.api.common.exception;

import com.auction.point.api.common.apipayload.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiException extends RuntimeException {

    private final BaseCode errorCode;
}
