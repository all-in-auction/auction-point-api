package com.auction.point.api.domain.point.controller;

import com.auction.point.api.common.apipayload.ApiResponse;
import com.auction.point.api.domain.payment.dto.response.ChargeResponseDto;
import com.auction.point.api.domain.payment.service.PaymentService;
import com.auction.point.api.domain.point.dto.request.ConvertRequestDto;
import com.auction.point.api.domain.point.dto.request.PointChangeRequestDto;
import com.auction.point.api.domain.point.dto.response.ConvertResponseDto;
import com.auction.point.api.domain.point.service.PointService;
import com.auction.point.api.feign.dto.response.CouponGetResponseDto;
import com.auction.point.api.feign.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

import static com.auction.point.api.common.constants.Const.USER_ID;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "PointController")
public class PointController {
    @Value("${payment.client.key}")
    private String CLIENT_KEY;
    private final PaymentService paymentService;
    private final PointService pointService;
    private final CouponService couponService;

    /**
     * 유저 포인트 생성
     *
     * @param userId
     */
    @PostMapping("/internal/v4/points")
    @ResponseBody
    @Operation(summary = "유저 포인트 생성", description = "회원가입 시, 유저 포인트를 생성하는 API", hidden = true)
    @Parameters({
            @Parameter(name = USER_ID, description = "유저 ID", example = "100000")
    })
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "요청에 성공하였습니다.", content = @Content(mediaType = "application/json"))
    public ApiResponse<Void> createPoint(@RequestHeader(USER_ID) long userId) {
        pointService.createPoint(userId);
        return ApiResponse.ok(null);
    }

    /**
     * 유저 포인트 변경 (경매 낙찰, 보증금 환불 등)
     *
     * @param userId
     * @param pointChangeRequestDto
     */
    @PatchMapping("/internal/v4/points")
    @ResponseBody
    @Operation(summary = "유저 포인트 변경", description = "포인트 사용, 환불 등으로 인한 포인트 변경 API", hidden = true)
    @Parameters({
            @Parameter(name = USER_ID, description = "유저 ID", example = "1")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    examples = {
                            @ExampleObject(value = "{\"amount\": 1000, \"paymentType\": \"SPEND\"}")
                    }
            )
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "요청에 성공하였습니다.", content = @Content(mediaType = "application/json"))
    public ApiResponse<Void> changePoint(
            @RequestHeader(USER_ID) long userId,
            @RequestBody PointChangeRequestDto pointChangeRequestDto
    ) {
        pointService.changePoint(userId, pointChangeRequestDto);
        return ApiResponse.ok(null);
    }

    /**
     * 포인트 충전
     *
     * @param userId
     * @param amount
     * @param couponUserId
     * @param model
     * @return front page
     */
    @GetMapping("/v2/points/buy")
    @Operation(summary = "유저 포인트 충전", description = "토스 페이를 이용한 포인트 충전 API")
    @Parameters({
            @Parameter(name = USER_ID, description = "유저 ID", example = "1"),
            @Parameter(name = "amount", description = "충전할 포인트양", example = "10000"),
            @Parameter(name = "couponUserId", description = "유저 ID"),
    })
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "요청에 성공하였습니다.", content = @Content(mediaType = "html")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "결제 금액은 1000원 단위입니다.", content = @Content(mediaType = "application/json"))
    })
    public String getPaymentPage(@RequestHeader(USER_ID) long userId,
                                 @RequestParam int amount,
                                 @RequestParam(required = false) Long couponUserId,
                                 Model model) {

        paymentService.validateAmount(amount);

        int paymentAmount = amount;

        if (couponUserId != null) {
            ApiResponse<CouponGetResponseDto> response = couponService.getValidCoupon(userId, couponUserId);
            CouponGetResponseDto couponGetResponseDto = response.getData();

            paymentAmount = amount * (100 - couponGetResponseDto.getDiscountRate()) / 100;
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
     *
     * @param jsonBody
     * @return ChargeResponseDto
     * @throws IOException
     */
    @PostMapping("/v1/points/buy/confirm")
    @ResponseBody
    @Operation(summary = "결제 승인", description = "포인트 충전 후, 프론트에서 호출하는 결제 승인 API", hidden = true)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "요청에 성공하였습니다.", content = @Content(mediaType = "application/json"))
    public ApiResponse<ChargeResponseDto> confirmPayment(@RequestBody String jsonBody) throws IOException {
        ChargeResponseDto chargeResponseDto = pointService.confirmPayment(jsonBody);

        return ApiResponse.ok(chargeResponseDto);
    }

    /**
     * 포인트 현금 전환
     *
     * @param userId
     * @param convertRequestDto
     * @return ConvertResponseDto
     */
    @PostMapping("/v1/points/to-cash")
    @ResponseBody
    @Operation(summary = "현금 전환", description = "포인트를 현금으로 전환하는 API")
    @Parameters({
            @Parameter(name = USER_ID, description = "유저 ID", example = "1")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    examples = {
                            @ExampleObject(value = "{\"amount\": 1000, \"bankCode\": \"신한\", \"bankAccount\": \"111-111-111111\"}")
                    }
            )
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "요청에 성공하였습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "권한이 없습니다.", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "현재 포인트 잔고보다 더 큰 값을 전환 요청할 수 없습니다.", content = @Content(mediaType = "application/json"))
    })
    public ApiResponse<ConvertResponseDto> convertPoint(@RequestHeader(USER_ID) long userId,
                                                        @Valid @RequestBody ConvertRequestDto convertRequestDto) {
        return ApiResponse.ok(pointService.convertPoint(userId, convertRequestDto));
    }
}
