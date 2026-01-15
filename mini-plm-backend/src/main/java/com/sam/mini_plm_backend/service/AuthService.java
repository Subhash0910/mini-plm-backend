package com.sam.mini_plm_backend.service;

import com.sam.mini_plm_backend.enums.Role;
import com.sam.mini_plm_backend.dto.LoginRequest;
import com.sam.mini_plm_backend.dto.SignupRequest;
import com.sam.mini_plm_backend.dto.AuthResponse;
import com.sam.mini_plm_backend.entity.User;
import com.sam.mini_plm_backend.repository.UserRepository;
import com.sam.mini_plm_backend.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse signup(SignupRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            logger.warn("Signup failed: Username '{}' already exists", request.getUsername());
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            logger.warn("Signup failed: Email '{}' already exists", request.getEmail());
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.VIEWER)
                // Default role
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);
        logger.info("User '{}' registered successfully with role: {}", savedUser.getUsername(), savedUser.getRole());
        
        String token = jwtUtil.generateToken(savedUser.getUsername());

        return AuthResponse.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().toString())
                .token(token)
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        try {
            logger.debug("Attempting login for user: {}", request.getUsername());
            
            // Authenticate using Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            // Fetch user from repository
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> {
                        logger.error("User not found in database: {}", request.getUsername());
                        return new RuntimeException("User not found");
                    });

            // Check if user is active
            if (!user.getIsActive()) {
                logger.warn("Login attempt for deactivated user: {}", request.getUsername());
                throw new RuntimeException("User account is deactivated");
            }

            // Generate JWT token
            String token = jwtUtil.generateToken(user.getUsername());
            logger.info("User '{}' logged in successfully", user.getUsername());

            return AuthResponse.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRole().toString())
                    .token(token)
                    .build();
                    
        } catch (BadCredentialsException e) {
            logger.warn("Invalid credentials for user: {}", request.getUsername());
            throw new RuntimeException("Invalid username or password");
        } catch (Exception e) {
            logger.error("Authentication error for user: {}", request.getUsername(), e);
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }
}
