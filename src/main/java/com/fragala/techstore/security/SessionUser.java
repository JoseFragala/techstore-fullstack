package com.fragala.techstore.security;

import java.io.Serializable;
import java.security.Principal;

public record SessionUser(Long id, String email, String role) implements Principal, Serializable {
    @Override
    public String getName() {
        return id.toString();
    }
}
