package com.ccommit.auction_server.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class SwaggerConfig {
    @Profile({"dev", "test"})
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .addServersItem(new Server().url("http://localhost:8080"))
                .addServersItem(new Server().url("aws 링크"))
                .info(new Info().title("Auction server back-end")
                        .description("Auction server API 명세서")
                        .version("v1.0.0")
                        .license(new License().name("Auction Server wiki document").url("https://github.com/gamsayeon/Auction-Server/wiki")))
                .externalDocs(new ExternalDocumentation()
                        .description("Auction Server Github URL")
                        .url("https://github.com/gamsayeon/Auction-Server"));
    }
}