package com.sam.mini_plm_backend.security;

import com.sam.mini_plm_backend.entity.User;
import com.sam.mini_plm_backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Custom UserDetailsService
 * 
 * Implements Spring Security's UserDetailsService to load user details from database.
 * Used by JWT authentication filter and authentication manager.
 */
@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Load user by username
     * 
     * Called by Spring Security to load user details during authentication.
     * 
     * @param username the username to load
     * @return UserDetails object with user information and authorities
     * @throws UsernameNotFoundException if user not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user details for username: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found: {}", username);
                    return new UsernameNotFoundException("User not found with username: " + username);
                });

        // Check if user is active
        if (!user.getIsActive()) {
            log.warn("User account is disabled: {}", username);
            throw new UsernameNotFoundException("User account is disabled: " + username);
        }

        log.debug("User details loaded successfully for username: {}", username);
        return buildUserDetails(user);
    }

    /**
     * Build Spring Security UserDetails from User entity
     * 
     * @param user the User entity
     * @return UserDetails object
     */
    private UserDetails buildUserDetails(User user) {
        // Create authority for user's role
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().getAuthority()));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.getIsActive())
                .build();
    }
}
