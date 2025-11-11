package com.example.routie_be.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Routie API", version = "v1"),
        security = { @SecurityRequirement(name = "Bearer Authentication") }
)

@SecurityScheme(
        name = "Bearer Authentication",
        description = "JWT를 입력하세요. 예: Bearer eyJhbGciOiJIUzI1NiJ9...",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)

public class SwaggerConfig {
}


