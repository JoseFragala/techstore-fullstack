package com.fragala.techstore.mapper;

import org.springframework.stereotype.Component;

import com.fragala.techstore.dto.response.UserResponse;
import com.fragala.techstore.entity.User;

@Component
public class UserMapper {

    private final RoleMapper roleMapper;

    public UserMapper(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.isActive(),
                roleMapper.toResponse(user.getRole()),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}