package com.womensafety.userservice;

import org.springframework.beans.factory.annotation.Value;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserServiceApplication {

	@Value("${jwt.secret:NOT_FOUND}")
	private String secret;

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

	@PostConstruct
	public void checkSecret() {
		System.out.println(">>> From main app, jwt.secret = " + secret);
	}
}