package com.auction.point.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;


@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    private final Environment environment;

    @Bean
    public OpenAPI openAPI() {
        String jwt = "JWT";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);
        Components components = new Components().addSecuritySchemes(jwt, new SecurityScheme()
                .name(jwt)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
        );

        String activeProfile = environment.getProperty("spring.profiles.active", "dev");
        String gatewayURL = "";
        if ("prod".equals(activeProfile)) {
            gatewayURL = "http://www.all-in-auction.site";
        } else {
            gatewayURL = "http://localhost:8080";
        }
        Server gatewayServer = new Server();
        gatewayServer.setUrl(gatewayURL);
        gatewayServer.setDescription("Gateway Server");

        return new OpenAPI()
                .addServersItem(gatewayServer)
                .info(apiInfo())
                .addSecurityItem(securityRequirement)
                .components(components);
    }

    private Info apiInfo() {
        return new Info()
                .title("ALL IN AUCTION - POINT API")
                .description("API documentation for Point Service")
                .version("v4");
    }
}