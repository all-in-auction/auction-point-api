package com.auction.point.api.domain.point.controller;

import com.auction.point.api.common.apipayload.ApiResponse;
import com.auction.point.api.domain.payment.dto.response.ChargeResponseDto;
import com.auction.point.api.domain.payment.service.PaymentService;
import com.auction.point.api.domain.point.dto.request.ConvertRequestDto;
import com.auction.point.api.domain.point.dto.response.ConvertResponseDto;
import com.auction.point.api.domain.point.service.PointService;
import com.auction.point.api.feign.dto.response.CouponGetResponseDto;
import com.auction.point.api.feign.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

//@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class PointController {
    @Value("${payment.client.key}")
    private String CLIENT_KEY;
    private final PaymentService paymentService;
    private final PointService pointService;

    private final CouponService couponService;

    /**
     * 포인트 충전
     * @param userId
     * @param amount
     * @param couponId
     * @param model
     * @return front page
     */
    @GetMapping("/v2/points/buy")
    public String getPaymentPage(@RequestHeader("X-User-Agent") long userId,
                                 @RequestParam int amount,
                                 @RequestParam(required = false) Long couponId,
                                 Model model) {

        paymentService.validateAmount(amount);

        int paymentAmount = amount;
        Long couponUserId = null;

        if(couponId != null) {
            ApiResponse<CouponGetResponseDto> response = couponService.getCoupon(userId, couponId);

            // TODO(coupon) : 예외처리가 필요한지 아니면 coupon server에서 해결되는지 확인 필요

            CouponGetResponseDto couponGetResponseDto = response.getData();
            paymentAmount = amount * (100 - couponGetResponseDto.getDiscountRate()) / 100;
            couponUserId = couponGetResponseDto.getCouponId();
        }

        String orderId = UUID.randomUUID().toString().substring(0, 10);

        model.addAttribute("userId", userId);
        model.addAttribute("clientKey", CLIENT_KEY);
        model.addAttribute("amount", paymentAmount);
        model.addAttribute("orderId", orderId);

        paymentService.createPayment(orderId, userId, amount, paymentAmount, couponUserId);
        return "payment/checkout";
    }

    /**
     * 결제 승인
     * 프론트에서 호출
     * @param jsonBody
     * @return ChargeResponseDto
     * @throws IOException
     */
    @PostMapping("/v1/points/buy/confirm")
    @ResponseBody
    public ApiResponse<ChargeResponseDto> confirmPayment(@RequestBody String jsonBody) throws IOException {
        ChargeResponseDto chargeResponseDto = pointService.confirmPayment(jsonBody);

        return ApiResponse.ok(chargeResponseDto);
    }

    /**
     * 포인트 현금 전환
     * @param userId
     * @param convertRequestDto
     * @return ConvertResponseDto
     */
    @PostMapping("/v1/points/to-cash")
    @ResponseBody
    public ApiResponse<ConvertResponseDto> convertPoint(long userId,
                                                        @RequestBody ConvertRequestDto convertRequestDto) {
        return ApiResponse.ok(pointService.convertPoint(userId, convertRequestDto));
    }
}
