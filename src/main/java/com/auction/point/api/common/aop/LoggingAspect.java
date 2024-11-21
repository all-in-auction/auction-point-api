package com.auction.point.api.common.aop;

import com.auction.point.api.common.aop.dto.RequestLogDto;
import com.auction.point.api.common.exception.ApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.auction.point.api.common.constants.Const.USER_ID;

@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {
    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;

    private final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("within(*..*Controller)")
    public void onRequestSuccess() {
    }

    @AfterReturning("onRequestSuccess()")
    public Object successLogging(JoinPoint joinPoint) {
        RequestLogDto requestLogDto = setRequestLogDto();
        Object result = joinPoint.getArgs();
        logger.info(requestLogDtoToString(requestLogDto));

        return result;
    }

    @Pointcut("within(*..*Controller)")
    public void onRequestException() {
    }

    @AfterThrowing(pointcut = "onRequestException()", throwing = "ex")
    public void failureLogging(Exception ex) {
        RequestLogDto requestLogDto = setRequestLogDto();
        if (ex instanceof ApiException apiException) {
            requestLogDto.changeException(apiException.getErrorCode().getReasonHttpStatus().getMessage());
        } else {
            requestLogDto.changeException(ex.getMessage());
        }
        logger.error(requestLogDtoToString(requestLogDto));
    }

    private RequestLogDto setRequestLogDto() {
        RequestLogDto requestLogDto = RequestLogDto.of(request.getRequestURL().toString(), request.getMethod(), null, null);

        String userId = request.getHeader(USER_ID);
        if(Strings.isNotBlank(userId)) {
            requestLogDto.changeRequestUserId(Long.parseLong(userId));
        }

        return requestLogDto;
    }

    private String requestLogDtoToString(RequestLogDto logDto) {
        Map map = objectMapper.convertValue(logDto, Map.class);
        return map.toString();
    }
}
