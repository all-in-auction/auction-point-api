package com.auction.point.api.domain.pointHistory.entity;

import com.auction.point.api.common.entity.TimeStamped;
import com.auction.point.api.domain.pointHistory.enums.PaymentType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "point_history")
@NoArgsConstructor
public class PointHistory extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private long userId;

    @NotNull
    private int price;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    private PointHistory(long userId, int price, PaymentType paymentType) {
        this.userId = userId;
        this.price = price;
        this.paymentType = paymentType;
    }

    public static PointHistory of(long userId, int price, PaymentType paymentType) {
        return new PointHistory(userId, price, paymentType);
    }
}
