package com.sam.mini_plm_backend.config;

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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Modern syntax
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // Modern syntax
                .authorizeHttpRequests(authz -> authz
                        // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/health").permitAll()

                        // Part endpoints - Role-based
                        .requestMatchers(HttpMethod.GET, "/api/parts/**")
                        .hasAnyRole("ADMIN", "ENGINEER", "VIEWER")
                        .requestMatchers(HttpMethod.POST, "/api/parts")
                        .hasAnyRole("ADMIN", "ENGINEER")
                        .requestMatchers(HttpMethod.PUT, "/api/parts/**")
                        .hasAnyRole("ADMIN", "ENGINEER")
                        .requestMatchers(HttpMethod.DELETE, "/api/parts/**")
                        .hasRole("ADMIN")

                        // BOM endpoints - Role-based
                        .requestMatchers(HttpMethod.GET, "/api/bom/**")
                        .hasAnyRole("ADMIN", "ENGINEER", "VIEWER")
                        .requestMatchers(HttpMethod.POST, "/api/bom/**")
                        .hasAnyRole("ADMIN", "ENGINEER")
                        .requestMatchers(HttpMethod.DELETE, "/api/bom/**")
                        .hasAnyRole("ADMIN", "ENGINEER")

                        // Change endpoints - Role-based
                        .requestMatchers(HttpMethod.GET, "/api/changes/**")
                        .hasAnyRole("ADMIN", "ENGINEER", "VIEWER")
                        .requestMatchers(HttpMethod.POST, "/api/changes")
                        .hasRole("ENGINEER")
                        .requestMatchers(HttpMethod.PUT, "/api/changes/**")
                        .hasRole("ENGINEER")

                        // Admin endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
