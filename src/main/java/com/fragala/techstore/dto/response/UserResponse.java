package com.fragala.techstore.dto.response;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@NoArgsConstructor 
public class UserResponse {

    private Long id;

    private String name;

    private String email;

    private String phone;

    private boolean active;

    private RoleResponse role;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    
}