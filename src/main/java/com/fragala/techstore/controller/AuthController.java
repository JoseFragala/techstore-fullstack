package com.fragala.techstore.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import com.fragala.techstore.dto.request.LoginRequest;
import com.fragala.techstore.dto.response.UserResponse;
import com.fragala.techstore.exception.ApiError;
import com.fragala.techstore.security.SessionUser;
import com.fragala.techstore.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final SessionAuthenticationStrategy sessionStrategy;
    private final SecurityContextRepository contextRepository;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager,
            SessionAuthenticationStrategy sessionStrategy,
            SecurityContextRepository contextRepository, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.sessionStrategy = sessionStrategy;
        this.contextRepository = contextRepository;
        this.userService = userService;
    }

    @GetMapping("/csrf")
    public CsrfResponse csrf(CsrfToken token) {
        return new CsrfResponse(token.getHeaderName(), token.getToken());
    }

    @PostMapping("/login")
    public UserResponse login(@Valid @RequestBody LoginRequest login,
            HttpServletRequest request, HttpServletResponse response) {
        var authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken.unauthenticated(login.email(), login.password()));
        sessionStrategy.onAuthentication(authentication, request, response);
        var context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        contextRepository.saveContext(context, request, response);
        return userService.findById(((SessionUser) authentication.getPrincipal()).id());
    }

    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal SessionUser user) {
        return userService.findById(user.id());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> invalidCredentials() {
        return ResponseEntity.status(401).body(new ApiError(401, "Invalid email or password"));
    }

    public record CsrfResponse(String headerName, String token) { }
}
