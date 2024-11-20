package com.auction.point.api.domain.point.entity;

import com.auction.point.api.common.entity.TimeStamped;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "point")
public class Point extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "point_amount")
    private int pointAmount;

    @NotNull
    @Column(name = "user_id", unique = true)
    private long userId;

    public void addPoint(int amount) {
        this.pointAmount += amount;
    }

    public void minusPoint(int amount) {
        this.pointAmount -= amount;
    }

    public void changePoint(int amount) {
        this.pointAmount = amount;
    }

    public Point(int pointAmount, long userId) {
        this.pointAmount = pointAmount;
        this.userId = userId;
    }

    public static Point of(int pointAmount, long userId) {
        return new Point(pointAmount, userId);
    }
}
