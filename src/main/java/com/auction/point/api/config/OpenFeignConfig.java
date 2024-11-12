package com.auction.point.api.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients("com.auction.point.api.feign.service")
public class OpenFeignConfig {
}
