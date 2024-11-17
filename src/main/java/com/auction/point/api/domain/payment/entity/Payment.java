package com.auction.point.api.domain.payment.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "order_id")
    private String orderId;

    @NotNull
    @Column(name = "user_id")
    private long userId;

    @NotNull
    @Column(name = "point_amount")
    private int pointAmount;

    @NotNull
    @Column(name = "payment_amount")
    private int paymentAmount;

    @Column(name = "coupon_user_id")
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
