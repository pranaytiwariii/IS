package com.example.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthBackendApplication {

	private static final Logger logger = LoggerFactory.getLogger(AuthBackendApplication.class);

	public static void main(String[] args) {
		logger.info("Starting Auth Backend Application...");
		SpringApplication.run(AuthBackendApplication.class, args);
		logger.info("Auth Backend Application started successfully!");
	}

}
