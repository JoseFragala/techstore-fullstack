package com.fragala.techstore.config;

import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        String algorithmId = "pbkdf2@SpringSecurity_v5_8";
        return new DelegatingPasswordEncoder(algorithmId, Map.of(
            algorithmId, Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8()));
    }
}
