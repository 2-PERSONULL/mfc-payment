package com.mfc.payment.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {
	@Bean
	public OpenAPI openAPI() {
		SecurityScheme apiKey = new SecurityScheme()
				.type(SecurityScheme.Type.HTTP)
				.in(SecurityScheme.In.HEADER)
				.name("Authorization")
				.scheme("bearer")
				.bearerFormat("JWT");

		SecurityRequirement securityRequirement = new SecurityRequirement().addList("Bearer Token");

		return new OpenAPI()
				.addServersItem(new Server().url("/"))
				.addServersItem(new Server().url("/payment-service"))
				.components(new Components()
						.addSecuritySchemes("Bearer Token", apiKey))
				.security(Arrays.asList(securityRequirement))
				.info(apiInfo());
	}

	private Info apiInfo() {
		return new Info()
				.title("Personull Payment")
				.description("Personull Payment Swagger UI")
				.version("1.0.0");
	}
}
