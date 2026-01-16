package com.sam.mini_plm_backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * CORS Configuration Properties
 * 
 * Maps properties from application.properties:
 * - app.cors.allowed-origins: Comma-separated list of allowed origins
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.cors")
public class CorsProperties {
    private List<String> allowedOrigins;
}
