package com.auction.point.api.domain.point.repository;

import com.auction.point.api.domain.point.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PointRepository extends JpaRepository<Point, Long> {
    @Query("SELECT pointAmount FROM Point WHERE userId = ?1")
    int findPointByUserId(long userId);

    Optional<Point> findByUserId(long userId);
}
