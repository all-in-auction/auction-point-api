package com.auction.point.api.config;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Configuration
@Slf4j
@Profile("prod")
public class EcsConfig {

    @Bean
    public EurekaInstanceConfigBean eurekaInstanceConfig(InetUtils inetUtils){

        EurekaInstanceConfigBean config = new EurekaInstanceConfigBean(inetUtils);

        config.setPreferIpAddress(true);
        config.setAppname("points-service");
        String taskArn = System.getenv("ECS_CONTAINER_METADATA_URI_V4");
        String ip = getPrivateIp(taskArn);
        config.setIpAddress(ip);
        config.setInstanceId("points-service:"+ip);

        return config;
    }

    public static String getPrivateIp(String metadataUrl) {
        try {
            // 메타데이터 API 요청
            URL url = new URL(metadataUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // 응답 읽기
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // JSON 파싱
            JSONObject metadata = new JSONObject(response.toString());
            JSONArray networks = metadata.getJSONArray("Networks");
            if (networks.length() > 0) {
                JSONObject network = networks.getJSONObject(0);
                JSONArray ipv4Addresses = network.getJSONArray("IPv4Addresses");
                if (ipv4Addresses.length() > 0) {
                    return ipv4Addresses.getString(0); // 첫 번째 IP 주소 반환
                }
            }
            log.info("No private IP address found in metadata.");
            return null;
        } catch (Exception e) {
            log.info("Failed to fetch private IP from metadata", e);
            return null;
        }
    }
}
