package com.example.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class AuthBackendApplication extends SpringBootServletInitializer {

	private static final Logger logger = LoggerFactory.getLogger(AuthBackendApplication.class);

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		logger.info("Configuring Auth Backend Application for WAR deployment...");
		return application.sources(AuthBackendApplication.class);
	}

	public static void main(String[] args) {
		logger.info("Starting Auth Backend Application...");
		SpringApplication.run(AuthBackendApplication.class, args);
		logger.info("Auth Backend Application started successfully!");
	}

}
