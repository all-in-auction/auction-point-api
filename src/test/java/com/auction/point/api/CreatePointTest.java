package com.auction.point.api;

import com.auction.point.api.domain.point.entity.Point;
import com.auction.point.api.domain.point.repository.PointRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
@SpringBootTest
@ActiveProfiles("dev")
public class CreatePointTest {

    @Autowired
    PointRepository pointRepository;

    @Test
    void 포인트_생성() {
        for (int i = 1; i <= 1000; i++) {
            Point point = new Point(100000000, i);
            pointRepository.save(point);
        }
    }
}
