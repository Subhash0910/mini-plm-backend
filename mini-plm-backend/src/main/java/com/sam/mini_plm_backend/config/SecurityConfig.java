package com.sam.mini_plm_backend.config;

import com.sam.mini_plm_backend.security.JwtAuthenticationEntryPoint;
import com.sam.mini_plm_backend.security.JwtAuthenticationFilter;
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
import java.util.List;

/**
 * Spring Security configuration
 * Handles JWT authentication, CORS, and request authorization
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                         JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Configure security filter chain
     * - Enable CORS
     * - Disable CSRF (not needed for stateless JWT API)
     * - Configure JWT authentication
     * - Set up authorization rules
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Enable CORS with centralized configuration
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Disable CSRF - not needed for stateless JWT API
                .csrf(csrf -> csrf.disable())
                // Use stateless session (JWT doesn't need sessions)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Configure exception handling
                .exceptionHandling(exc -> exc
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                // Configure authorization
                .authorizeHttpRequests(authz -> authz
                        // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/health").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // Parts endpoints - role-based
                        .requestMatchers(HttpMethod.GET, "/api/parts/**").hasAnyRole("VIEWER", "ENGINEER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/parts").hasAnyRole("ENGINEER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/parts/**").hasAnyRole("ENGINEER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/parts/**").hasAnyRole("ENGINEER", "ADMIN")
                        .requestMatchers("/api/parts/**/promote").hasAnyRole("ENGINEER", "ADMIN")
                        .requestMatchers("/api/parts/**/revise").hasAnyRole("ENGINEER", "ADMIN")
                        .requestMatchers("/api/parts/**/obsolete").hasAnyRole("ENGINEER", "ADMIN")
                        // BOM endpoints
                        .requestMatchers(HttpMethod.GET, "/api/bom/**").hasAnyRole("VIEWER", "ENGINEER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/bom/**").hasAnyRole("ENGINEER", "ADMIN")
                        // Change endpoints
                        .requestMatchers(HttpMethod.GET, "/api/changes/**").hasAnyRole("VIEWER", "ENGINEER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/changes/**").hasAnyRole("ENGINEER", "ADMIN")
                        // All other requests require authentication
                        .anyRequest().authenticated()
                );

        // Add JWT filter before UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Centralized CORS configuration
     * Replaces @CrossOrigin on individual controllers
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allowed origins
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:3001",
                "http://localhost:8080",
                "https://mini-plm-frontend.onrender.com",
                "https://mini-plm-backend.onrender.com"
        ));
        
        // Allowed HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // Allowed headers
        configuration.setAllowedHeaders(Arrays.asList(
                "Content-Type",
                "Authorization",
                "X-Requested-With",
                "Accept",
                "Origin"
        ));
        
        // Expose headers to client
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type"
        ));
        
        // Allow credentials (cookies, authorization headers, etc.)
        configuration.setAllowCredentials(true);
        
        // Cache CORS configuration for 1 hour
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Authentication manager bean
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Password encoder bean
     * Using BCryptPasswordEncoder for secure password hashing
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
