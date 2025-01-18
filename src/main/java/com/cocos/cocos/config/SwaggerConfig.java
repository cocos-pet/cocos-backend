package com.cocos.cocos.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    private static final String SWAGGER_DESCRIPTION = """
            Cocos API Docs
            """;

    @Bean
    public OpenAPI openAPI() {

        SecurityScheme apiKey = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")
                .scheme("bearer")
                .bearerFormat("JWT");


        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("Bearer Token");

        Server server = new Server();
        server.setUrl("https://www.cocos.r-e.kr");

        Server server1 = new Server();
        server.setUrl("http://localhost:8080");

        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("Bearer Token", apiKey))
                .addSecurityItem(securityRequirement)
                .info(apiInfo())
                .servers(List.of(server, server1));
    }

    private Info apiInfo() {
        return new Info()
                .title("COCOS API Swagger")
                .description(SWAGGER_DESCRIPTION)
                .version("dev");
    }


}
