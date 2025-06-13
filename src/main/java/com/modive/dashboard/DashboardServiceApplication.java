package com.modive.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableFeignClients(basePackages = "com.modive.dashboard")
@SpringBootApplication
@EnableAsync
public class DashboardServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DashboardServiceApplication.class, args);
	}

}
