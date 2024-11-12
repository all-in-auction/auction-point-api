package com.auction.point.api.domain.payment.service;

import com.auction.point.api.common.apipayload.status.ErrorStatus;
import com.auction.point.api.common.exception.ApiException;
import com.auction.point.api.domain.payment.entity.Payment;
import com.auction.point.api.domain.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public void createPayment(String orderId, long userId, int pointAmount,
                              int paymentAmount, Long couponUserId) {
        Payment payment = Payment.of(orderId, userId, pointAmount, paymentAmount, couponUserId);
        paymentRepository.save(payment);
    }

    public void validateAmount(int amount) {
        if (amount < 1000 || amount % 1000 != 0) {
            throw new ApiException(ErrorStatus._INVALID_AMOUNT_REQUEST);
        }
    }

    public Payment getPayment(String orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ApiException(ErrorStatus._INVALID_REQUEST));
    }
}
