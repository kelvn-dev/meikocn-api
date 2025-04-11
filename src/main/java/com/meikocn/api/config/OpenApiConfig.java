package com.meikocn.api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

// Swagger-ui endpoint: http://localhost:8080/swagger-ui/index.html#
@Configuration
@OpenAPIDefinition(
    info =
        @Info(
            title = "Meikocn API",
            version = "v1.0",
            contact = @Contact(name = "Kelvin Vu", url = "https://github.com/kelvn-dev")),
    servers = @Server(url = "/"))
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer")
public class OpenApiConfig {}
