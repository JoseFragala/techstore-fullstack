package com.fragala.techstore.mapper;
import com.fragala.techstore.dto.response.RoleResponse;
import com.fragala.techstore.entity.Role;

import org.springframework.stereotype.Component;


@Component 
public class RoleMapper {

    public RoleResponse toResponse(Role role){
        RoleResponse response = new RoleResponse();

        response.setId(role.getId());
        response.setName(role.getName());
        response.setDescription(role.getDescription());
        response.setCreatedAt(role.getCreatedAt());

        return response;


    }
    
}
