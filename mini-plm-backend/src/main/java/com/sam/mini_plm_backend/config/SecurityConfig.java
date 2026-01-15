package com.sam.mini_plm_backend.config;

import com.sam.mini_plm_backend.security.JwtAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:5173}")
    private String allowedOrigins;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        logger.info("SecurityConfig initialized");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Parse allowed origins from properties
        String[] origins = allowedOrigins.split(",");
        configuration.setAllowedOrigins(Arrays.asList(origins));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        logger.info("CORS configured for origins: {}", allowedOrigins);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            logger.warn("Authentication error: {}", authException.getMessage());
                            response.sendError(401, "Unauthorized");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            logger.warn("Access denied: {}", accessDeniedException.getMessage());
                            response.sendError(403, "Access Denied");
                        })
                )
                .authorizeHttpRequests(authz -> authz
                        // ========== PUBLIC ENDPOINTS ==========
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/health").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // ========== PART ENDPOINTS ==========
                        .requestMatchers(HttpMethod.GET, "/api/parts/**")
                        .hasAnyRole("ADMIN", "ENGINEER", "VIEWER")
                        .requestMatchers(HttpMethod.POST, "/api/parts")
                        .hasAnyRole("ADMIN", "ENGINEER")
                        .requestMatchers(HttpMethod.PUT, "/api/parts/**")
                        .hasAnyRole("ADMIN", "ENGINEER")
                        .requestMatchers(HttpMethod.DELETE, "/api/parts/**")
                        .hasRole("ADMIN")

                        // ========== BOM ENDPOINTS ==========
                        .requestMatchers(HttpMethod.GET, "/api/bom/**")
                        .hasAnyRole("ADMIN", "ENGINEER", "VIEWER")
                        .requestMatchers(HttpMethod.POST, "/api/bom/**")
                        .hasAnyRole("ADMIN", "ENGINEER")
                        .requestMatchers(HttpMethod.DELETE, "/api/bom/**")
                        .hasAnyRole("ADMIN", "ENGINEER")

                        // ========== CHANGE ENDPOINTS ==========
                        .requestMatchers(HttpMethod.GET, "/api/changes/**")
                        .hasAnyRole("ADMIN", "ENGINEER", "VIEWER")
                        .requestMatchers(HttpMethod.POST, "/api/changes")
                        .hasRole("ENGINEER")
                        .requestMatchers(HttpMethod.PUT, "/api/changes/**")
                        .hasRole("ENGINEER")

                        // ========== ADMIN ENDPOINTS ==========
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        logger.info("SecurityFilterChain configured successfully");
        return http.build();
    }
}
