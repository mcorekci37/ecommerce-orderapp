package com.emce.ecommerce;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableCaching
@EnableJpaAuditing
@OpenAPIDefinition(
		info = @Info(
				title = "E-commerce Order API",
				version = "1.0.0",
				description = "API documentation for the e-commerce order service"
		),
		servers = @Server(
				url = "http://localhost:8080",
				description = "E-commerce Order API URL"

		),
		security = @SecurityRequirement(name = "Authorization")
)
@SecurityScheme(
		name = "Authorization",
		type = SecuritySchemeType.APIKEY,
		in = SecuritySchemeIn.HEADER,
		bearerFormat = "JWT",
		scheme = "bearer",
		description = "JWT Authorization header using the Bearer scheme. Example: 'Bearer <your_token>'"
)
public class EcommerceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcommerceApplication.class, args);
	}

}
