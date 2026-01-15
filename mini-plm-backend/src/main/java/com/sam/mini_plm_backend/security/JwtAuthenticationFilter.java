package com.sam.mini_plm_backend.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * CRITICAL FIX:
     * Do NOT run JWT validation for public endpoints like /auth/**.
     * The context-path=/api is prepended by Spring Boot, so we check for /auth/**
     * otherwise signup/login will return 401 even if SecurityConfig says permitAll().
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        logger.debug("Checking filter bypass for path: {}", path);

        boolean shouldSkip = path.startsWith("/api/auth/")
                || path.startsWith("/api/health")
                || path.startsWith("/swagger-ui/")
                || path.startsWith("/v3/api-docs")
                || path.equals("/api")
                || path.equals("/");
        
        if (shouldSkip) {
            logger.debug("Skipping JWT filter for path: {}", path);
        }
        
        return shouldSkip;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            String token = extractTokenFromRequest(request);

            if (StringUtils.hasText(token)) {
                if (jwtUtil.isTokenValid(token)) {
                    String username = jwtUtil.getUsernameFromToken(token);
                    logger.debug("Setting authentication for user: {}", username);

                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    username,
                                    null,
                                    new ArrayList<>()
                            );

                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                } else {
                    logger.warn("Invalid JWT token for request: {}", request.getRequestURI());
                }
            }
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("Cannot set user authentication: {}", e.getMessage(), e);
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from the Authorization header
     * Expected format: "Bearer <token>"
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}
