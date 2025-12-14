package com.example.reflection;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Reflection API")
                        .version("v1")
                        .description("CRUD operations for Sample entities")
                        .license(new License().name("Unspecified").url("https://example.com"))
                );
    }
}
