package com.fragala.techstore.dto.response;

import java.time.LocalDateTime;

public record UserResponse(

        Long id,

        String name,

        String email,

        String phone,

        boolean active,

        RoleResponse role,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {
}
