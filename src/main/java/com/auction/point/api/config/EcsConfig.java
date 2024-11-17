package com.auction.point.api.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
@Slf4j
@Profile("prod")
public class EcsConfig {

    @Bean
    public EurekaInstanceConfigBean eurekaInstanceConfig(InetUtils inetUtils){

        EurekaInstanceConfigBean config = new EurekaInstanceConfigBean(inetUtils);
        String ip = null;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
            log.info("ECS Task Container Private Ip address is {}", ip);
        } catch (UnknownHostException e) {
            log.info("ECS Task Container Private Ip address can not found");
            e.printStackTrace();
        }

        config.setIpAddress(ip);
        config.setPreferIpAddress(true);
        config.setAppname("points-service");
        String taskArn = System.getenv("ECS_CONTAINER_METADATA_URI_V4");
        String instanceId = taskArn != null
                ? String.format("points-service:%s:%s", ip, taskArn)
                : String.format("points-service:%s", ip);

        config.setInstanceId(instanceId);

        return config;
    }
}
