package com.demo.docker;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MS1 Spring Boot Client")
                        .version("1.0.0")
                        .description("Spring Boot Microservice acting as client consuming MS2 Target REST APIs with RestTemplate, OAuth2 tokens, and streaming."));
    }
}
