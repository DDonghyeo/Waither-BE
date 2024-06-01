package com.waither.weatherservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI waitherAPI() {
		Info info = new Info()
			.title("Waither Weather API Docs")
			.description("Waither Weather Service API 명세서입니다.")
			.version("1.0.0");

		return new OpenAPI()
			.addServersItem(new Server().url("/"))
			.info(info);
	}
}
