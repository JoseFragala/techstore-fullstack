package com.fragala.techstore.support;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/** Isolates HTTP contract tests; real access rules are tested with PostgreSQL and cookies. */
@TestConfiguration(proxyBeanMethods = false)
@EnableWebSecurity
public class ControllerTestSecurityConfig {
    @Bean
    SecurityFilterChain controllerTestChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(auth -> auth.anyRequest().authenticated()).build();
    }
}
