package com.auction.point.grpc.utility;

import io.grpc.stub.StreamObserver;

public class GrpcErrorHandler {

    public static void handleGrpcError(StreamObserver<?> responseObserver, Exception e) {
        responseObserver.onError(e);
    }
}
