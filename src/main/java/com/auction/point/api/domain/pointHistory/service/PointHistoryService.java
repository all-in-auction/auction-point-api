package com.auction.point.api.domain.pointHistory.service;

import com.auction.point.api.domain.pointHistory.entity.PointHistory;
import com.auction.point.api.domain.pointHistory.enums.PaymentType;
import com.auction.point.api.domain.pointHistory.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointHistoryService {
    private final PointHistoryRepository pointHistoryRepository;

    @Transactional
    public PointHistory createPointHistory(long userId, int price, PaymentType paymentType) {
        PointHistory pointHistory = PointHistory.of(userId, price, paymentType);
        return pointHistoryRepository.save(pointHistory);
    }

}
