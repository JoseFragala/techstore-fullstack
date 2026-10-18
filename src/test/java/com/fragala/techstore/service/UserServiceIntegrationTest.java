package com.fragala.techstore.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import com.fragala.techstore.dto.request.CreateUserRequest;
import com.fragala.techstore.repository.RoleRepository;
import com.fragala.techstore.repository.UserRepository;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceIntegrationTest {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long userId;

    @AfterEach
    void cleanUp() {
        if (userId != null) {
            userRepository.deleteById(userId);
        }
    }

    @Test
    void changePassword_shouldPersistHashesOnCreationAndChange_whenUserExists() {
        Long roleId = roleRepository.findAll().stream()
            .filter(role -> role.getName().equals("CUSTOMER"))
            .findFirst().orElseThrow().getId();
        String originalPassword = "original-integration-password";
        userId = userService.create(new CreateUserRequest("Password test user",
            "password-" + UUID.randomUUID() + "@example.com", originalPassword,
            "11999990000", roleId)).id();

        String originalHash = userRepository.findById(userId).orElseThrow().getPassword();
        assertNotEquals(originalPassword, originalHash);
        assertTrue(passwordEncoder.matches(originalPassword, originalHash));

        String newPassword = "new-integration-password";
        userService.changePassword(userId, newPassword);

        String newHash = userRepository.findById(userId).orElseThrow().getPassword();
        assertNotEquals(newPassword, newHash);
        assertNotEquals(originalHash, newHash);
        assertTrue(passwordEncoder.matches(newPassword, newHash));
        assertFalse(passwordEncoder.matches(originalPassword, newHash));
    }
}
