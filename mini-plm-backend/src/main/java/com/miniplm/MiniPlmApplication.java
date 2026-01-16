package com.miniplm;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * Mini PLM Application Entry Point
 * Production-grade Spring Boot Application
 */
@SpringBootApplication(scanBasePackages = {
        "com.miniplm",
        "com.sam.mini_plm_backend",
        "com.sam.miniplmbackend"
})
public class MiniPlmApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiniPlmApplication.class, args);
    }

    /**
     * Configure OpenAPI (Swagger 3.0) documentation
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Mini PLM API")
                        .version("1.0.0")
                        .description("Product Lifecycle Management REST API")
                        .contact(new Contact()
                                .name("Mini PLM Support")
                                .email("support@miniplm.com")
                                .url("https://miniplm.com")
                        )
                )
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development Server"),
                        new Server()
                                .url("https://api.miniplm.com")
                                .description("Production Server")
                ));
    }
}
