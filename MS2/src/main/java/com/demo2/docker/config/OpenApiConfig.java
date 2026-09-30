package com.demo2.docker.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    public static final String BEARER_AUTH = "BearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Content Manager Migration - Target REST API")
                        .version("1.0.0")
                        .description("Spring Boot REST APIs designed to replace legacy IBM Content Manager direct SDK calls " +
                                "and manual Db2 SQL queries. Features OAuth2 Client-Credentials authentication, " +
                                "multipart document ingestion, binary streaming (InputStream/OutputStream), " +
                                "and legacy SQL gap-analysis verification.")
                        .contact(new Contact()
                                .name("Legacy Migration Engineering Team")
                                .email("migration-team@example.com"))
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH))
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                                .name(BEARER_AUTH)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter your Bearer token obtained from POST /oauth/token")));
    }
}
