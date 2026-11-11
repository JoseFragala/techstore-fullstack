package com.fragala.techstore.security;

import java.util.List;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fragala.techstore.repository.UserRepository;

@Component
public class UserAuthenticationProvider implements AuthenticationProvider {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final String missingUserHash;

    public UserAuthenticationProvider(UserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
        this.missingUserHash = encoder.encode("unused-credential-comparison");
    }

    @Override
    @Transactional(readOnly = true)
    public Authentication authenticate(Authentication authentication) {
        String password = authentication.getCredentials().toString();
        var user = users.findByEmail(authentication.getName());
        boolean matches;
        try {
            matches = encoder.matches(password,
                user.map(value -> value.getPassword()).orElse(missingUserHash));
        } catch (IllegalArgumentException invalidStoredEncoding) {
            throw new BadCredentialsException("Invalid email or password");
        }
        if (user.isEmpty() || !matches || !user.get().isActive()) {
            throw new BadCredentialsException("Invalid email or password");
        }
        var account = user.get();
        var principal = new SessionUser(account.getId(), account.getEmail(), account.getRole().getName());
        return UsernamePasswordAuthenticationToken.authenticated(principal, null,
            List.of(new SimpleGrantedAuthority("ROLE_" + principal.role())));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
