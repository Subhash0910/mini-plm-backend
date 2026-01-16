package com.sam.mini_plm_backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI Configuration
 * 
 * Configures API documentation using Swagger/OpenAPI 3.0
 * Accessible at:
 * - UI: http://localhost:8080/api/swagger-ui/index.html
 * - JSON: http://localhost:8080/api/v3/api-docs
 * - YAML: http://localhost:8080/api/v3/api-docs.yaml
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Mini PLM Backend API",
        version = "1.0.0",
        description = "Complete Product Lifecycle Management System Backend API\n\n" +
                "This API provides comprehensive PLM functionality including:\n" +
                "- User Authentication and Authorization\n" +
                "- Product Management\n" +
                "- Document Control\n" +
                "- Change Management\n" +
                "- Workflow Management\n\n" +
                "**Default Test Credentials:**\n" +
                "- Admin: admin / admin123\n" +
                "- Manager: manager / manager123\n" +
                "- User: user / user123\n",
        contact = @Contact(
            name = "Mini PLM Team",
            email = "support@company.com",
            url = "https://company.com"
        )
    ),
    servers = {
        @Server(
            url = "http://localhost:8080",
            description = "Local Development Server"
        ),
        @Server(
            url = "https://api.company.com",
            description = "Production Server"
        )
    }
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "JWT Authentication Token\n\n" +
            "1. Login with username and password via /api/auth/login\n" +
            "2. Copy the returned JWT token\n" +
            "3. Click 'Authorize' and paste the token in format: Bearer <token>\n" +
            "4. All subsequent requests will include the token automatically\n"
)
public class SwaggerConfig {
    // Configuration is handled by annotations
}
