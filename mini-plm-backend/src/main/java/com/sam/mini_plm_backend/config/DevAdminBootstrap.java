package com.sam.mini_plm_backend.config;

import com.sam.mini_plm_backend.enums.Role;
import com.sam.mini_plm_backend.entity.User;
import com.sam.mini_plm_backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Windchill-like bootstrap: in local/dev, ensure there is always a Site Admin account
 * so roles can be managed through UI without manual DB edits.
 */
@Component
@Profile("local")
public class DevAdminBootstrap implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DevAdminBootstrap.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.admin.username:admin}")
    private String adminUsername;

    @Value("${app.bootstrap.admin.password:Admin@1234567}")
    private String adminPassword;

    @Value("${app.bootstrap.admin.email:admin@mini-plm.local}")
    private String adminEmail;

    public DevAdminBootstrap(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        try {
            if (userRepository.existsByUsername(adminUsername)) {
                logger.info("DevAdminBootstrap: admin user '{}' already exists", adminUsername);
                return;
            }

            User admin = new User();
            admin.setUsername(adminUsername);
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);

            userRepository.save(admin);

            logger.warn("DevAdminBootstrap: created local admin user '{}' (password from app.bootstrap.admin.password)", adminUsername);
        } catch (Exception e) {
            logger.error("DevAdminBootstrap: failed to create admin user", e);
        }
    }
}
