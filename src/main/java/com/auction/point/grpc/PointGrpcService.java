package com.auction.point.grpc;

import com.auction.point.api.domain.point.service.PointService;
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

    @Override
    @Transactional
    public void createPoint(
            Point.CreatePointRequest request,
            StreamObserver<Point.CreatePointResponse> responseObserver)
    {
        try {
            long userId = request.getUserId();
            pointService.createPoint(userId);
            log.info("SUCCESS");
            Point.CreatePointResponse response = Point.CreatePointResponse.newBuilder()
                    .setStatus("SUCCESS")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
}
