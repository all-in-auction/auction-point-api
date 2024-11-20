package com.auction.point.api.domain.point.service;

import com.auction.point.api.common.apipayload.status.ErrorStatus;
import com.auction.point.api.common.exception.ApiException;
import com.auction.point.api.domain.payment.dto.response.ChargeResponseDto;
import com.auction.point.api.domain.payment.entity.Payment;
import com.auction.point.api.domain.payment.service.PaymentService;
import com.auction.point.api.domain.point.dto.request.ConvertRequestDto;
import com.auction.point.api.domain.point.dto.request.PointChangeRequestDto;
import com.auction.point.api.domain.point.dto.response.ConvertResponseDto;
import com.auction.point.api.domain.point.entity.Point;
import com.auction.point.api.domain.point.repository.PointRepository;
import com.auction.point.api.domain.pointHistory.entity.PointHistory;
import com.auction.point.api.domain.pointHistory.enums.PaymentType;
import com.auction.point.api.domain.pointHistory.service.PointHistoryService;
import com.auction.point.api.feign.dto.request.CouponUseRequestDto;
import com.auction.point.api.feign.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointService {
    @Value("${payment.secret.key}")
    private String API_SECRET_KEY;
    private final PointRepository pointRepository;
    private final PaymentService paymentService;
    private final PointHistoryService pointHistoryService;

    private final CouponService couponService;

    @Transactional
    public ChargeResponseDto confirmPayment(String jsonBody) throws IOException {
        JSONObject response = sendRequest(parseRequestData(jsonBody), API_SECRET_KEY, "https://api.tosspayments.com/v1/payments/confirm");
        if (response.containsKey("error")) {
            throw new ApiException(ErrorStatus._INVALID_PAY_REQUEST);
        }

        String orderId = response.get("orderId").toString();
        Payment payment = paymentService.getPayment(orderId);
        long userId = payment.getUserId();
        Point point = pointRepository.findByUserId(userId).orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_USER));

        // point history 생성 및 저장
        PointHistory pointHistory = pointHistoryService.createPointHistory(userId, payment.getPointAmount(), PaymentType.CHARGE);

        // point 보유량 변화
        point.addPoint(payment.getPointAmount());

        // coupon 사용 저장
        if (payment.getCouponUserId() != null) {
            couponService.useCoupon(userId, payment.getCouponUserId(), CouponUseRequestDto.from(pointHistory));
        }

        return new ChargeResponseDto(payment.getPaymentAmount(), payment.getPointAmount(), point.getPointAmount());
    }

    @Transactional
    public void createPoint(long userId) {
        Point point = new Point(0, userId);
        try {
            pointRepository.save(point);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public ConvertResponseDto convertPoint(long userId, ConvertRequestDto convertRequestDto) {
        Point point = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_USER));
        if (convertRequestDto.getAmount() > point.getPointAmount()) {
            throw new ApiException(ErrorStatus._INVALID_CONVERT_REQUEST);
        }

        // point history 생성 및 저장
        pointHistoryService.createPointHistory(userId, convertRequestDto.getAmount(), PaymentType.TRANSFER);

        // point 보유량 변화
        point.minusPoint(convertRequestDto.getAmount());

        return new ConvertResponseDto(convertRequestDto.getAmount(), point.getPointAmount());
    }

    private Point getPoint(long userId) {
        return pointRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(ErrorStatus._INVALID_REQUEST));
    }

    @Transactional
    public void decreasePoint(long userId, int price) {
        Point point = getPoint(userId);
        int newPointAmount = point.getPointAmount() - price;
        point.changePoint(newPointAmount);
        pointRepository.save(point);
    }

    @Transactional
    public void increasePoint(long userId, int price) {
        Point point = getPoint(userId);
        int newPointAmount = point.getPointAmount() + price;
        point.changePoint(newPointAmount);
        pointRepository.save(point);
    }

    @Transactional
    public void changePoint(long userId, PointChangeRequestDto pointChangeRequestDto) {
        int amount = pointChangeRequestDto.getAmount();
        PaymentType paymentType = pointChangeRequestDto.getPaymentType();

        if (PaymentType.isDecreasePoint(paymentType)) {
            // 포인트 차감
            decreasePoint(userId, amount);
        } else {
            // 포인트 증감
            increasePoint(userId, amount);
        }

        // point history 생성 및 저장
        pointHistoryService.createPointHistory(userId, amount, paymentType);
    }

    private JSONObject sendRequest(JSONObject requestData, String secretKey, String urlString) throws IOException {
        HttpURLConnection connection = createConnection(secretKey, urlString);
        try (OutputStream os = connection.getOutputStream()) {
            os.write(requestData.toString().getBytes(StandardCharsets.UTF_8));
        }

        try (InputStream responseStream = connection.getResponseCode() == 200 ? connection.getInputStream() : connection.getErrorStream();
             Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8)) {
            return (JSONObject) new JSONParser().parse(reader);
        } catch (Exception e) {
            log.error("Error reading response", e);
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("error", "Error reading response");
            return errorResponse;
        }
    }

    private HttpURLConnection createConnection(String secretKey, String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8)));
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        return connection;
    }

    private JSONObject parseRequestData(String jsonBody) {
        try {
            return (JSONObject) new JSONParser().parse(jsonBody);
        } catch (ParseException e) {
            log.error("JSON Parsing Error", e);
            return new JSONObject();
        }
    }
}
