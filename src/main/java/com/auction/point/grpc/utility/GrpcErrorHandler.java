package com.auction.point.grpc.utility;

import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

public class GrpcErrorHandler {

    public static <T> void handleGrpcError(StreamObserver<T> responseObserver, StatusRuntimeException exception) {
        responseObserver.onError(exception);
    }
}
