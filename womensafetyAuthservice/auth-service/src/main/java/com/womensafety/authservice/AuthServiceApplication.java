package com.womensafety.authservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableScheduling
@SpringBootApplication(scanBasePackages = "com.womensafety.authservice")
public class AuthServiceApplication {

	public static void main(String[] args) {
		log.info("App booting from main()");
		SpringApplication.run(AuthServiceApplication.class, args);
	}


}
