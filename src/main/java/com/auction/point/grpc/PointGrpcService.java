package com.auction.point.grpc;

import com.auction.point.api.common.apipayload.status.ErrorStatus;
import com.auction.point.api.common.exception.ApiException;
import com.auction.point.api.domain.point.repository.PointRepository;
import com.auction.point.api.domain.point.service.PointService;
import com.auction.point.api.domain.pointHistory.enums.PaymentType;
import com.auction.point.api.domain.pointHistory.service.PointHistoryService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import point.Point;
import point.PointServiceGrpc;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointGrpcService extends PointServiceGrpc.PointServiceImplBase {

    private final PointService pointService;
    private final PointRepository pointRepository;
    private final PointHistoryService pointHistoryService;

    @Override
    @Transactional
    public void createPoint(
            Point.CreatePointRequest request,
            StreamObserver<Point.CreatePointResponse> responseObserver) {

        Point.CreatePointResponse.Builder responseBuilder = Point.CreatePointResponse.newBuilder();
        try {
            long userId = request.getUserId();
            pointService.createPoint(userId);
            responseBuilder.setStatus("SUCCESS");
        } catch (Exception e) {
            log.error(e.getMessage());
            responseBuilder.setStatus("ERROR");
        } finally {
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getPoints(Point.GetPointsRequest request, StreamObserver<Point.GetPointsResponse> responseObserver) {
        Point.GetPointsResponse.Builder responseBuilder = Point.GetPointsResponse.newBuilder();
        try {
            long userId = request.getUserId();
            com.auction.point.api.domain.point.entity.Point point = pointRepository.findByUserId(userId)
                    .orElseThrow(() -> new ApiException(ErrorStatus._INVALID_REQUEST));

            responseBuilder.setTotalPoint(point.getPointAmount());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApiException(ErrorStatus._INVALID_REQUEST);
        } finally {
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        }

    }

    @Override
    public void decreasePoints(Point.DecreasePointsRequest request, StreamObserver<Point.DecreasePointsResponse> responseObserver) {
        Point.DecreasePointsResponse.Builder responseBuilder = Point.DecreasePointsResponse.newBuilder();
        try {
            long userId = request.getUserId();
            int price = request.getAmount();
            pointService.decreasePoint(userId, price);
            responseBuilder.setStatus("SUCCESS");
        } catch (Exception e) {
            log.error(e.getMessage());
            responseBuilder.setStatus("FAIL");
        } finally {
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void createPointHistory(Point.CreatePointHistoryRequest request, StreamObserver<Point.CreatePointHistoryResponse> responseObserver) {
        Point.CreatePointHistoryResponse.Builder responseBuilder = Point.CreatePointHistoryResponse.newBuilder();
        try {
            long userId = request.getUserId();
            int price = request.getAmount();
            String paymentType = request.getPaymentType();
            pointHistoryService.createPointHistory(userId, price, PaymentType.valueOf(paymentType));
            responseBuilder.setStatus("SUCCESS");
        } catch (Exception e) {
            log.error(e.getMessage());
            responseBuilder.setStatus("FAIL");
        } finally {
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        }
    }
}
