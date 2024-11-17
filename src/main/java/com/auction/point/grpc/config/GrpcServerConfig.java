package com.auction.point.grpc.config;


import com.auction.point.grpc.PointGrpcService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class GrpcServerConfig {

    @Bean
    public Server grpcServer(PointGrpcService pointGrpcService) throws IOException {
        Server server = ServerBuilder.forPort(8085)
                .addService(pointGrpcService) // gRPC 서비스 등록
                .build()
                .start();

        System.out.println("gRPC Server started on port 8081");
        return server;
    }
}

