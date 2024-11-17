package com.auction.point.api.domain.point.entity;

import com.auction.point.api.common.entity.TimeStamped;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "point")
@NoArgsConstructor
public class Point extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int pointAmount;

    @NotNull
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
