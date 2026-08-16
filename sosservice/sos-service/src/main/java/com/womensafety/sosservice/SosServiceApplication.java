package com.womensafety.sosservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.retry.annotation.EnableRetry;

@EnableScheduling
@EnableRetry
@SpringBootApplication
public class SosServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(
				SosServiceApplication.class,
				args
		);
	}
}