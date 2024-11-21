package com.auction.point.grpc;

import com.auction.Point;
import com.auction.PointServiceGrpc;
import com.auction.point.api.common.exception.ApiException;
import com.auction.point.api.domain.point.repository.PointRepository;
import com.auction.point.api.domain.point.service.PointService;
import com.auction.point.api.domain.pointHistory.enums.PaymentType;
import com.auction.point.api.domain.pointHistory.service.PointHistoryService;
import com.auction.point.grpc.utility.GrpcErrorHandler;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointGrpcService extends PointServiceGrpc.PointServiceImplBase {

    private final PointService pointService;
    private final PointRepository pointRepository;
    private final PointHistoryService pointHistoryService;

    @Override
    public void getPoints(Point.GetPointsRequest request, StreamObserver<Point.GetPointsResponse> responseObserver) {
        try {
            long userId = request.getUserId();
            com.auction.point.api.domain.point.entity.Point point = pointRepository.findByUserId(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

            Point.GetPointsResponse response = Point.GetPointsResponse.newBuilder()
                    .setTotalPoint(point.getPointAmount())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (ApiException e) {
            GrpcErrorHandler.handleGrpcError(responseObserver,
                    Status.INVALID_ARGUMENT.withDescription(
                            e.getErrorCode().getReasonHttpStatus().getMessage()).asRuntimeException());
        } catch (Exception e) {
            log.error("Unexpected exception in getPoints: {}", e.getMessage(), e);
            GrpcErrorHandler.handleGrpcError(responseObserver,
                    Status.INTERNAL.withDescription("Unexpected error").asRuntimeException());
        }
    }

    @Override
    public void decreasePoints(Point.DecreasePointsRequest request, StreamObserver<Point.DecreasePointsResponse> responseObserver) {
        try {
            long userId = request.getUserId();
            int price = request.getAmount();
            pointService.decreasePoint(userId, price);

            Point.DecreasePointsResponse response = Point.DecreasePointsResponse.newBuilder()
                    .setStatus("SUCCESS")
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error in decreasePoints: {}", e.getMessage(), e);
            GrpcErrorHandler.handleGrpcError(responseObserver, Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void createPointHistory(Point.CreatePointHistoryRequest request, StreamObserver<Point.CreatePointHistoryResponse> responseObserver) {
        try {
            long userId = request.getUserId();
            int price = request.getAmount();
            Point.PaymentType paymentType = request.getPaymentType();

            pointHistoryService.createPointHistory(userId, price, PaymentType.valueOf(paymentType.name()));

            Point.CreatePointHistoryResponse response = Point.CreatePointHistoryResponse.newBuilder()
                    .setStatus("SUCCESS")
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error in createPointHistory: {}", e.getMessage(), e);
            GrpcErrorHandler.handleGrpcError(responseObserver, Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void increasePoint(Point.IncreasePointRequest request, StreamObserver<Point.IncreasePointResponse> responseObserver) {
        try {
            long userId = request.getUserId();
            int price = request.getAmount();

            pointService.increasePoint(userId, price);

            Point.IncreasePointResponse response = Point.IncreasePointResponse.newBuilder()
                    .setStatus("SUCCESS")
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error in createPointHistory: {}", e.getMessage(), e);
            GrpcErrorHandler.handleGrpcError(responseObserver, Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }
}
