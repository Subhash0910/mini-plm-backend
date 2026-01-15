package com.sam.mini_plm_backend.controller;

import com.sam.mini_plm_backend.dto.UpdateUserRoleRequest;
import com.sam.mini_plm_backend.dto.UserSummary;
import com.sam.mini_plm_backend.entity.User;
import com.sam.mini_plm_backend.repository.UserRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserController.class);

    private final UserRepository userRepository;

    public AdminUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<UserSummary>> listUsers() {
        List<UserSummary> users = userRepository.findAll().stream()
                .map(u -> new UserSummary(u.getId(), u.getUsername(), u.getEmail(), u.getRole()))
                .toList();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{username}/role")
    public ResponseEntity<?> updateRole(@PathVariable String username, @Valid @RequestBody UpdateUserRoleRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        user.setRole(request.getRole());
        userRepository.save(user);

        logger.info("Admin updated role: username={} role={}", username, request.getRole());
        return ResponseEntity.ok(new UserSummary(user.getId(), user.getUsername(), user.getEmail(), user.getRole()));
    }
}
