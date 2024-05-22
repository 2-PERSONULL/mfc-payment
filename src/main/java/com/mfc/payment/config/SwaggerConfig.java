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

		SecurityRequirement securityRequirement = new SecurityRequirement().addList("Bearer Token") ;// 여기까지 다 추가

		return new OpenAPI()
			.addServersItem(new Server().url("/member-service"))
			.components(new Components()
				.addSecuritySchemes("Bearer Token", apiKey))
			.security(Arrays.asList(securityRequirement));
	}

	private Info apiInfo() {
		return new Info()
			.title("Springdoc 테스트")
			.description("Springdoc을 사용한 Swagger UI 테스트")
			.version("1.0.0");
	}

}
