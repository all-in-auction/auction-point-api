package com.auction.point.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableDiscoveryClient
@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = "com.auction.*")
public class AuctionPointApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuctionPointApiApplication.class, args);
	}

}
