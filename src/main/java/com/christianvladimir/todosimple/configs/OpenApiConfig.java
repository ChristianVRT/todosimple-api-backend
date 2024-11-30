package com.christianvladimir.todosimple.configs;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!prd")
public class OpenApiConfig {

    private static final String SCHEME_NAME= "Bearer";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("api-todo-simple")
                        .version("v1")
                        .description("todo simple API Swagger")
                        .contact(new Contact()
                                .name("Christian Vladimir")
                                .email("christianvladimir@gmail.com")
                        ))
                .addSecurityItem(new SecurityRequirement().addList(SCHEME_NAME))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes(SCHEME_NAME,
                                new SecurityScheme()
                                        .name("Authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}