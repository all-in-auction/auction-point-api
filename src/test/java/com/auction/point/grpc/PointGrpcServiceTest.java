package com.auction.point.grpc;

import com.auction.Point;
import com.auction.point.api.domain.point.repository.PointRepository;
import com.auction.point.api.domain.point.service.PointService;
import com.auction.point.api.domain.pointHistory.enums.PaymentType;
import com.auction.point.api.domain.pointHistory.service.PointHistoryService;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PointGrpcServiceTest {

    @Mock
    private PointService pointService;

    @Mock
    private PointRepository pointRepository;

    @Mock
    private PointHistoryService pointHistoryService;

    @Mock
    private StreamObserver<Point.GetPointsResponse> getPointsResponseObserver;

    @Mock
    private StreamObserver<Point.DecreasePointsResponse> decreasePointsResponseObserver;

    @Mock
    private StreamObserver<Point.CreatePointHistoryResponse> createPointHistoryResponseObserver;

    @Mock
    private StreamObserver<Point.IncreasePointResponse> increasePointResponseObserver;

    @InjectMocks
    private PointGrpcService pointGrpcService;

    @Test
    void 포인트조회시_존재하지않는_유저_예외출력() {

        //given
        Long userId = 1L;

        Point.GetPointsRequest request = Point.GetPointsRequest.newBuilder()
                .setUserId(userId)
                .build();

        given(pointRepository.findByUserId(userId)).willReturn(Optional.empty());

        // when
        pointGrpcService.getPoints(request, getPointsResponseObserver);

        // then
        ArgumentCaptor<Throwable> captor = ArgumentCaptor.forClass(Throwable.class);
        Mockito.verify(getPointsResponseObserver, Mockito.times(1)).onError(captor.capture());

        Throwable capturedException = captor.getValue();
        io.grpc.StatusRuntimeException statusException = (io.grpc.StatusRuntimeException) capturedException;

        // 예외 메시지와 상태 코드 출력
        if (capturedException instanceof io.grpc.StatusRuntimeException) {
            System.out.println("Captured status: " + statusException.getStatus().getCode());
            System.out.println("Captured message: " + statusException.getMessage());
        } else {
            System.out.println("Captured exception: " + capturedException);
        }

        assertEquals("INTERNAL: Unexpected error", statusException.getMessage());
    }

    @Test
    void 포인트조회시_존재하지않는_유저_예외_생기는지_확인() {
        // given
        Long userId = 1L;

        Point.GetPointsRequest request = Point.GetPointsRequest.newBuilder()
                .setUserId(userId)
                .build();

        given(pointRepository.findByUserId(userId)).willReturn(Optional.empty());

        // when
        pointGrpcService.getPoints(request, getPointsResponseObserver);

        // then
        // responseObserver.onError가 호출되었는지 확인
        Mockito.verify(getPointsResponseObserver, Mockito.times(1)).onError(Mockito.any());
    }

    @Test
    void 포인트조회시_성공() {
        // given
        Long userId = 1L;
        int pointAmount = 1000;

        Point.GetPointsRequest request = Point.GetPointsRequest.newBuilder()
                .setUserId(userId)
                .build();

        Point.GetPointsResponse response = Point.GetPointsResponse.newBuilder()
                .setTotalPoint(pointAmount)
                .build();

        com.auction.point.api.domain.point.entity.Point point =
                new com.auction.point.api.domain.point.entity.Point(pointAmount, userId);

        given(pointRepository.findByUserId(userId)).willReturn(Optional.of(point));
        getPointsResponseObserver.onNext(response);

        // when
        pointGrpcService.getPoints(request, getPointsResponseObserver);

        // then
        Mockito.verify(getPointsResponseObserver, Mockito.times(1)).onCompleted();
    }


    @Test
    void 포인트_감소시_예외발생() {
        // given
        Long userId = 1L;
        int amount = 1000;

        Point.DecreasePointsRequest request = Point.DecreasePointsRequest.newBuilder()
                .setUserId(userId)
                .setAmount(amount)
                .build();

        // pointService.decreasePoint에서 예외 발생하도록 설정
        Mockito.doThrow(new RuntimeException("Insufficient points"))
                .when(pointService).decreasePoint(userId, amount);

        // when
        pointGrpcService.decreasePoints(request, decreasePointsResponseObserver);

        // then
        Mockito.verify(decreasePointsResponseObserver, Mockito.times(1)).onError(Mockito.any());
    }


    @Test
    void 포인트_감소시_성공() {
        // given
        Long userId = 1L;
        int amount = 1000;

        Point.DecreasePointsRequest request = Point.DecreasePointsRequest.newBuilder()
                .setUserId(userId)
                .setAmount(amount)
                .build();

        Point.DecreasePointsResponse response = Point.DecreasePointsResponse.newBuilder()
                .setStatus("SUCCESS")
                .build();

        // when
        pointGrpcService.decreasePoints(request, decreasePointsResponseObserver);

        // then
        Mockito.verify(pointService, Mockito.times(1)).decreasePoint(userId, amount);

        // onNext 호출 확인 및 응답 데이터 검증
        Mockito.verify(decreasePointsResponseObserver, Mockito.times(1))
                .onNext(Mockito.argThat(argument ->
                        argument instanceof Point.DecreasePointsResponse &&
                                argument.getStatus().equals("SUCCESS")
                ));

        // onCompleted 호출 확인
        Mockito.verify(decreasePointsResponseObserver, Mockito.times(1)).onCompleted();

        // onError 호출되지 않았는지 확인
        Mockito.verify(decreasePointsResponseObserver, Mockito.never()).onError(Mockito.any());
    }



    @Test
    void 포인트_히스토리_생성시_예외발생() {
        // given
        Long userId = 1L;
        int amount = 1000;
        int price = 1000;

        Point.CreatePointHistoryRequest request = Point.CreatePointHistoryRequest.newBuilder()
                .setUserId(userId)
                .setAmount(amount)
                .setPaymentType(Point.PaymentType.SPEND)
                .build();

        // pointService.decreasePoint에서 예외 발생하도록 설정
        Mockito.doThrow(new RuntimeException("Error"))
                .when(pointHistoryService).createPointHistory(userId, price, PaymentType.REFUND);

        // when
        pointGrpcService.createPointHistory(request, createPointHistoryResponseObserver);

        // then
        Mockito.verify(createPointHistoryResponseObserver, Mockito.times(1)).onError(Mockito.any());
    }

    @Test
    void 포인트_히스토리_생성_성공() {
        // given
        long userId = 1L;
        int amount = 1000;
        Point.PaymentType paymentType = Point.PaymentType.REFUND;

        Point.CreatePointHistoryRequest request = Point.CreatePointHistoryRequest.newBuilder()
                .setUserId(userId)
                .setAmount(amount)
                .setPaymentType(paymentType)
                .build();

        // when
        pointGrpcService.createPointHistory(request, createPointHistoryResponseObserver);

        // then
        Mockito.verify(pointHistoryService, Mockito.times(1))
                .createPointHistory(userId, amount, PaymentType.valueOf(paymentType.name()));

        // onNext 호출 확인 및 응답 데이터 검증
        Mockito.verify(createPointHistoryResponseObserver, Mockito.times(1))
                .onNext(Mockito.argThat(argument ->
                        argument instanceof Point.CreatePointHistoryResponse &&
                                argument.getStatus().equals("SUCCESS")
                ));

        // onCompleted 호출 확인
        Mockito.verify(createPointHistoryResponseObserver, Mockito.times(1)).onCompleted();

        // onError 호출되지 않았는지 확인
        Mockito.verify(createPointHistoryResponseObserver, Mockito.never()).onError(Mockito.any());
    }


    @Test
    void 포인트_증가시_예외발생() {
        // given
        Long userId = 1L;
        int amount = 1000;
        int price = 1000;

        Point.IncreasePointRequest request = Point.IncreasePointRequest.newBuilder()
                .setUserId(userId)
                .setAmount(amount)
                .build();

        // pointService.decreasePoint에서 예외 발생하도록 설정
        Mockito.doThrow(new RuntimeException("Error"))
                .when(pointService).increasePoint(userId, price);

        // when
        pointGrpcService.increasePoint(request, increasePointResponseObserver);

        // then
        Mockito.verify(increasePointResponseObserver, Mockito.times(1)).onError(Mockito.any());
    }

    @Test
    void 포인트_증가_성공() {
        // given
        long userId = 1L;
        int amount = 1000;

        Point.IncreasePointRequest request = Point.IncreasePointRequest.newBuilder()
                .setUserId(userId)
                .setAmount(amount)
                .build();

        // when
        pointGrpcService.increasePoint(request, increasePointResponseObserver);

        // then
        Mockito.verify(pointService, Mockito.times(1)).increasePoint(userId, amount);

        // onNext 호출 확인 및 응답 데이터 검증
        Mockito.verify(increasePointResponseObserver, Mockito.times(1))
                .onNext(Mockito.argThat(argument ->
                        argument instanceof Point.IncreasePointResponse &&
                                argument.getStatus().equals("SUCCESS")
                ));

        // onCompleted 호출 확인
        Mockito.verify(increasePointResponseObserver, Mockito.times(1)).onCompleted();

        // onError 호출되지 않았는지 확인
        Mockito.verify(increasePointResponseObserver, Mockito.never()).onError(Mockito.any());
    }

}