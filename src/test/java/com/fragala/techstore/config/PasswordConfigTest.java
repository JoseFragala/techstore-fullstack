package com.fragala.techstore.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class PasswordConfigTest {

    @Test
    void passwordEncoder_shouldUseSaltAndVerifyPassword_whenEncodingSamePasswordTwice() {
        PasswordEncoder encoder = new PasswordConfig().passwordEncoder();
        String password = "integration-test-password";
        String first = encoder.encode(password);
        String second = encoder.encode(password);

        assertNotEquals(password, first);
        assertNotEquals(first, second);
        assertTrue(first.startsWith("{pbkdf2@SpringSecurity_v5_8}"));
        assertTrue(encoder.matches(password, first));
        assertTrue(encoder.matches(password, second));
        assertFalse(encoder.matches("incorrect-test-password", first));
    }
}
