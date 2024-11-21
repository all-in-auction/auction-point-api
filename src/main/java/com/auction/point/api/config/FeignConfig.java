package com.auction.point.api.config;

import com.auction.point.api.feign.decoder.ApiErrorDecoder;
import feign.Client;
import feign.Request;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import feign.okhttp.OkHttpClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableFeignClients(basePackages = "com.auction.point.api.feign.service")
public class FeignConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new ApiErrorDecoder();
    }

    @Bean
    public Request.Options options() {
        return new Request.Options(
                1000,
                TimeUnit.MILLISECONDS,
                1000,
                TimeUnit.MILLISECONDS,
                false
        );
    }

    @Bean
    public Retryer retryer() {
        return new Retryer.Default(500L, TimeUnit.SECONDS.toMillis(5), 2);
    }
}
