package com.auction.point.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = "com.auction.*")
public class AuctionPointApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuctionPointApiApplication.class, args);
	}

}
