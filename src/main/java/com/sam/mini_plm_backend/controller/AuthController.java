package com.sam.mini_plm_backend.controller;

import com.sam.mini_plm_backend.dto.JwtResponse;
import com.sam.mini_plm_backend.dto.LoginRequest;
import com.sam.mini_plm_backend.entity.Role;
import com.sam.mini_plm_backend.entity.User;
import com.sam.mini_plm_backend.repository.UserRepository;
import com.sam.mini_plm_backend.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller
 * 
 * Handles user authentication and authorization endpoints.
 * - Login with username and password
 * - Token generation
 * - User registration
 * - Token refresh
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User authentication and authorization endpoints")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * User Login
     * 
     * Authenticates user with username and password.
     * Returns JWT token if authentication successful.
     */
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful", 
            content = @Content(schema = @Schema(implementation = JwtResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            log.info("Login attempt for user: {}", loginRequest.getUsername());

            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // Set authentication in context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT token
            String token = jwtTokenProvider.generateToken(loginRequest.getUsername());

            // Get user details
            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElse(null);

            log.info("User {} logged in successfully", loginRequest.getUsername());

            // Build response
            JwtResponse response = JwtResponse.builder()
                    .token(token)
                    .type("Bearer")
                    .id(user != null ? user.getId() : null)
                    .username(user != null ? user.getUsername() : loginRequest.getUsername())
                    .email(user != null ? user.getEmail() : null)
                    .role(user != null ? user.getRole() : null)
                    .build();

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            log.warn("Login failed - invalid credentials for user: {}", loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiErrorResponse("Invalid username or password", "INVALID_CREDENTIALS"));
        } catch (Exception e) {
            log.error("Login error for user: {}", loginRequest.getUsername(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiErrorResponse("Authentication failed", "AUTH_FAILED"));
        }
    }

    /**
     * User Registration
     * 
     * Creates a new user account.
     */
    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Register a new user account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request or user already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> register(@Valid @RequestBody LoginRequest registrationRequest) {
        try {
            log.info("Registration request for user: {}", registrationRequest.getUsername());

            // Check if user already exists
            if (userRepository.existsByUsername(registrationRequest.getUsername())) {
                log.warn("Registration failed - username already exists: {}", registrationRequest.getUsername());
                return ResponseEntity.badRequest()
                        .body(new ApiErrorResponse("Username already exists", "USER_EXISTS"));
            }

            // Create new user
            User newUser = User.builder()
                    .username(registrationRequest.getUsername())
                    .password(passwordEncoder.encode(registrationRequest.getPassword()))
                    .email(registrationRequest.getUsername() + "@company.com") // Default email
                    .role(Role.USER) // Default role
                    .isActive(true)
                    .build();

            userRepository.save(newUser);
            log.info("User {} registered successfully", registrationRequest.getUsername());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiSuccessResponse("User registered successfully"));

        } catch (Exception e) {
            log.error("Registration error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiErrorResponse("Registration failed", "REGISTRATION_ERROR"));
        }
    }

    /**
     * Get Current User
     * 
     * Returns authenticated user information.
     */
    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Get authenticated user information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User info retrieved"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<?> getCurrentUser() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("Error fetching current user", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiErrorResponse("User not found", "USER_NOT_FOUND"));
        }
    }

    /**
     * Health Check
     * 
     * Returns authentication service status.
     */
    @GetMapping("/health")
    @Operation(summary = "Auth service health", description = "Check authentication service health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(new ApiSuccessResponse("Authentication service is healthy"));
    }

    /**
     * API Error Response
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class ApiErrorResponse {
        private String message;
        private String code;
    }

    /**
     * API Success Response
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class ApiSuccessResponse {
        private String message;
    }
}
