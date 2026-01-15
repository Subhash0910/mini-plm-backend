package com.sam.mini_plm_backend.config;

import com.sam.mini_plm_backend.security.JwtAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
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
    private final UserDetailsService userDetailsService;

    @Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:5173}")
    private String allowedOrigins;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, UserDetailsService userDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
        logger.info("SecurityConfig initialized");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        String[] origins = allowedOrigins.split(",");
        configuration.setAllowedOrigins(Arrays.asList(origins));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
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
                        // FIX: Remove /api prefix - server.servlet.context-path=/api handles it
                        // Paths are evaluated relative to context-path
                        .requestMatchers(HttpMethod.POST, "/auth/signup").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/auth/health").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/health/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/", "/index.html", "/static/**", "/assets/**").permitAll()

                        // Protected endpoints - require authentication and specific roles
                        .requestMatchers(HttpMethod.GET, "/parts/**").hasAnyRole("ADMIN", "ENGINEER", "VIEWER")
                        .requestMatchers(HttpMethod.POST, "/parts").hasAnyRole("ADMIN", "ENGINEER")
                        .requestMatchers(HttpMethod.PUT, "/parts/**").hasAnyRole("ADMIN", "ENGINEER")
                        .requestMatchers(HttpMethod.DELETE, "/parts/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/bom/**").hasAnyRole("ADMIN", "ENGINEER", "VIEWER")
                        .requestMatchers(HttpMethod.POST, "/bom/**").hasAnyRole("ADMIN", "ENGINEER")
                        .requestMatchers(HttpMethod.DELETE, "/bom/**").hasAnyRole("ADMIN", "ENGINEER")

                        .requestMatchers(HttpMethod.GET, "/changes/**").hasAnyRole("ADMIN", "ENGINEER", "VIEWER")
                        .requestMatchers(HttpMethod.POST, "/changes").hasRole("ENGINEER")
                        .requestMatchers(HttpMethod.PUT, "/changes/**").hasRole("ENGINEER")

                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        logger.info("SecurityFilterChain configured successfully");
        return http.build();
    }
}
