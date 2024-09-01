/*
 * DigitalRx : com.ritwik.idme.IdmeApplication.java
 * Release : @SVN_RELEASE_NUMBER@
 * Copyright (c) 2022 H2HSolutions Inc, All Rights Reserved.
 * Redistribution and use in source and binary forms, with or without modification,
 * are not permitted without specific prior written permission.
 */

package com.ritwik.idme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class IdmeApplication  extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(IdmeApplication.class, args);
	}
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(IdmeApplication.class);
	}
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
