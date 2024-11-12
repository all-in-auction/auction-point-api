package com.auction.point.api.domain.payment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String orderId;

    @NotNull
    private long userId;

    @NotNull
    private int pointAmount;

    @NotNull
    private int paymentAmount;

    private Long couponUserId;

    private Payment(String orderId, long userId, int pointAmount, int paymentAmount, long couponUserId) {
        this.orderId = orderId;
        this.userId = userId;
        this.pointAmount = pointAmount;
        this.paymentAmount = paymentAmount;
        this.couponUserId = couponUserId;
    }

    private Payment(String orderId, long userId, int pointAmount, int paymentAmount) {
        this.orderId = orderId;
        this.userId = userId;
        this.pointAmount = pointAmount;
        this.paymentAmount = paymentAmount;
    }

    public static Payment of(String orderId, long userId, int pointAmount,
                             int paymentAmount, Long couponUserId) {
        if (couponUserId != null) {
            return new Payment(orderId, userId, pointAmount, paymentAmount, couponUserId);
        } else {
            return new Payment(orderId, userId, pointAmount, paymentAmount);
        }
    }
}
