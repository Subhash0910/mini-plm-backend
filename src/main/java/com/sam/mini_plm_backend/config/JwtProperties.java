package com.sam.mini_plm_backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT Configuration Properties
 * 
 * Maps properties from application.properties:
 * - app.jwt.secret: Secret key for signing
 * - app.jwt.expiration: Token expiration in milliseconds
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {
    private String secret;
    private long expiration;
}
